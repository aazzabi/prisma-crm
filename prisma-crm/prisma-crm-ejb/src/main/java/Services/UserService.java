package Services;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;

import Entities.Agent;
import Entities.Client;
import Entities.User;
import Enums.AccountState;
import Enums.Role;
import Interfaces.IUserLocal;
import Interfaces.IUserRemote;
import Utils.AuthJWT;
import Utils.JavaMailUtil;
import Utils.MD5Hash;

@Stateful
public class UserService implements IUserLocal, IUserRemote {

	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager entityManager;

	public static User UserLogged;

	public UserService() {

	}

	@Override
	public int createUser(User user) {
		user.setPassword(MD5Hash.getMD5Hash(user.getPassword()));
		String activationHashedCode = MD5Hash.getMD5Hash(user.getPhoneNumber() + user.getEmail());
		user.setConfirmationToken(activationHashedCode);
		user.setCreatedAt(new Date());
		try {
			JavaMailUtil.sendMail(user.getEmail(), "Confirmation code",
					"Your confirmation code : " + activationHashedCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		entityManager.persist(user);
		return user.getId();
	}

	@Override
	public boolean activateAccount(String confirmationToken) {
		Query query = entityManager.createQuery(
				"SELECT new User(u.id,u.confirmationToken) " + "FROM User u WHERE u.confirmationToken=:param");
		User u = null;
		try {
			u = (User) query.setParameter("param", confirmationToken).getSingleResult();

			if (u != null && u.getConfirmationToken().equals(confirmationToken)) {
				User userTochange = findUserById(u.getId());
				userTochange.setAccountState(AccountState.ACTIVATED);
				updateUser(userTochange);
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			System.out.println("\n\n\n\n\n\n confirmationToken Not Found | User Not Set \n\n\n\n\n\n ");
			return false;
		}

	}

	@Override
	public User findUserById(int id) {
		Query query = entityManager.createQuery(
				"SELECT new User(u.id,u.firstName,u.lastName,u.profileImage,u.phoneNumber,u.email,u.password,u.createdAt,u.accountState,u.confirmationToken) "
						+ "FROM User u WHERE u.id=:param");
		return (User) query.setParameter("param", id).getSingleResult();
	}

	@Override
	public List<User> findAllUsers() {
		Query query = entityManager.createQuery(
				"SELECT new User(u.id,u.firstName,u.lastName,u.profileImage,u.phoneNumber,u.email,u.password,u.createdAt,u.accountState,u.confirmationToken) "
						+ "FROM User u");
		return (List<User>) query.getResultList();
	}

	@Override
	public void updateUser(User user) {

		entityManager.merge(user);

	}

	@Override
	public void deleteUser(int id) {
		entityManager.remove(entityManager.find(User.class, id));

	}

	@Override
	public User loginUser(String email, String pwd) {

		String hashedPwd = MD5Hash.getMD5Hash(pwd);
		Query query = entityManager.createQuery(

				"SELECT u FROM User u WHERE (u.email=:uname AND u.password=:upwd) ");
		User user = (User) query.setParameter("uname", email).setParameter("upwd", hashedPwd).getSingleResult();
		Role a = user.getRole();
		this.UserLogged = user;

		return user;

	}

	@Override
	public boolean changePwd(User user, String oldPwd, String newPwd) {
		System.out.println("\n\n\n\n\n\n\n\n u : " + user.getId() + "\n\n\n\n\n\n\n\n\n");
		Query query = entityManager.createQuery(
				"SELECT new User(u.id,u.firstName,u.lastName,u.profileImage,u.phoneNumber,u.email,u.password,u.createdAt,u.accountState,u.confirmationToken) "
						+ "FROM User u WHERE  (u.id=:uid)");
		User userToChnage = (User) query.setParameter("uid", user.getId()).getSingleResult();
		if (user.getPassword().equals(MD5Hash.getMD5Hash(oldPwd))) {
			user.setPassword(MD5Hash.getMD5Hash(newPwd));
			entityManager.merge(user);
			return true;
		}
		return false;
	}

	@Override
	public boolean uploadProfileImage(String imgToUpload) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int AssignAdmin(int id) {
		User user = findUserById(id);
		user.setRole(Role.Admin);
		entityManager.merge(user);

		return user.getId();
	}

	@Override
	public int AssignClient(int id) {
		User user = findUserById(id);

		Client c = new Client();
		System.out.println("Khra ///////////////////:"+c.getId());
		c.setPassword(user.getPassword());
		c.setEmail(user.getEmail());
		c.setFirstName(user.getFirstName());
		c.setLastName(user.getLastName());
		c.setAccountState(user.getAccountState());
		c.setCreatedAt(user.getCreatedAt());
		c.setConfirmationToken(user.getConfirmationToken());
		c.setFidelityScore(0);
		entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
		entityManager.persist(c);
		entityManager.flush();
		return c.getId();
	}

	@Override
	public int AssignAgents(int id) {
		User user = findUserById(id);

		Agent c = new Agent();
		c.setPassword(user.getPassword());
		c.setEmail(user.getEmail());
		c.setFirstName(user.getFirstName());
		c.setLastName(user.getLastName());
		c.setAccountState(user.getAccountState());
		c.setCreatedAt(user.getCreatedAt());
		c.setConfirmationToken(user.getConfirmationToken());
		c.setSalary(0);
		entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
		entityManager.merge(c);
		return c.getId();
	}

}
