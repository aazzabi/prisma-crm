package Services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import Entities.ClientCart;
import Entities.ClientOrder;
import Entities.TemporaryInvoice;
import Enums.OrderType;
import Utils.Mailer;

@Singleton
public class WorkerBean {

	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager manager;
	int i = 0;
	// List of clients carts : this is mainly for handling forgotten carts
	List<ClientCart> notifyCarts = new ArrayList<ClientCart>();

	public void fetchForgottenCartsWithUnNotifiedViews() throws InterruptedException {
		@SuppressWarnings("unchecked")
		List<ClientCart> TMP = manager
				.createQuery("SELECT C FROM ClientCart C WHERE C.clientOrder=null AND C.isCheckedOut=:checkedOut")
				.setParameter("checkedOut", false).getResultList();
		System.out.println("List size : " + TMP.size());
		if ((TMP.size() > 0) && (TMP != null)) {
			if (notifyCarts.size() == 0) {
				for (ClientCart cart : TMP) {
					notifyCarts.add(cart);
					System.out.println("Element added in a freshly new list , size " + notifyCarts.size());
				}
			} else {
				for (ClientCart cart : TMP) {
					if (!searchForCart(cart)) {
						notifyCarts.add(cart);
						System.out.println("Element Added");
					} else {
						System.out.println("Element exists");
					}
				}

			}
		}

	}

	public boolean searchForCart(ClientCart cart) {
		for (ClientCart c : notifyCarts) {
			if (c.getId() == cart.getId()) {
				return true;
			}
		}
		return false;
	}

	public boolean sendEmailToNotifyAndSetToSeen() {
		if (notifyCarts.size() > 0) {
			List<Integer> indexes = new ArrayList<Integer>();
			System.out.println("Beggining of the phase : forgotten cart");
			for (ClientCart c : notifyCarts) {
				ClientCart TMP = manager.find(ClientCart.class, c.getId());
				if ((TMP != null) && (!TMP.isNotifiedAboutCartForgottenItems())) {
					c.setNotifiedAboutCartForgottenItems(true);
					manager.merge(c);
					sendEmailOfAbondonedCRT(c);
					indexes.add(new Integer(notifyCarts.indexOf(c)));
				}
			}
			manager.flush();
			for (Integer x : indexes) {
				System.out.println("Value of the index is " + x);
				notifyCarts.remove((int) x);
				System.out.println("Removed from the list , current size is " + notifyCarts.size());
			}
			return true;
		} else {
			System.out.println("List is empty ");
			return false;
		}
	}

	// Sends mail after 4 hours of an abondoned cart
	public boolean sendEmailOfAbondonedCRT(ClientCart destination) {
		try {
			String data = "<p>Bonjour Msr/Mme " + destination.getClient().getFirstName()
					+ "<br>Vous avez oublié votre panier sans le valider <br>Pour le valider il suffit de consulter ce URI : </p>"
					+ "http://localhost:9080/prisma-crm-web/main/cart/pass-to-checkout";
			Mailer.sendAsHtml(destination.getClient().getEmail(), " Panier Oublié ", data, null);
			return true;
		} catch (MessagingException m) {
			m.printStackTrace();
			return false;
		}

	}

	// banning clients whom surpassed the temporary invoices limit
	public void banningClientsWithUncheckedOrder() {
		@SuppressWarnings("unchecked")
		List<TemporaryInvoice> tempo = manager.createQuery("SELECT T FROM TemporaryInvoice T").getResultList();
		Date date = new Date();
		java.sql.Date currentDate = new java.sql.Date(date.getTime());
		if ((tempo.size() > 0)) {
			for (TemporaryInvoice invoice : tempo) {
				if ((currentDate.after(invoice.getDeadline())) && (invoice.getOrder() != null)
						&& (!invoice.getOrder().isValid())
						&& (!invoice.getOrder().getClient().isBannedFromLocalOrders())
						&& (invoice.getOrder().getOrderNature() == OrderType.LocalPaymentMethod)) {
					// Banning the user from passing any local payment
					invoice.getOrder().getClient().setBannedFromLocalOrders(true);
					manager.merge(invoice.getOrder().getClient());
					manager.remove(invoice);
					try {
						String data = "<p>Bonjour Msr/Mme " + invoice.getOrder().getClient().getFirstName()
								+ "<br>Vus que vous n'avez pas respectez la date limite pour confirmer votre commande , "
								+ "Nous avons l'immense regret de vous annoncer que vous n'avez plus le droit de passer des commandes locaux , "
								+ "veuillez contacter notre service compétent pour regler ce probléme</p>";
						Mailer.sendAsHtml(invoice.getOrder().getClient().getEmail(),
								" Dépassage de la durée tolérable pour la confirmation d'une commande ", data, null);
					} catch (MessagingException m) {
						m.printStackTrace();
					} finally {
						manager.flush();
						System.out.println("Le client " + invoice.getOrder().getClient().getFirstName() + " est bannis");
					}
				}
			}
		}
	}

}
