package Interfaces;

import java.io.IOException;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Remote;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import Entities.Invoice;
import Entities.Product;
import Entities.RepairRequest;
import Enums.RepairStatus;

@Remote
@LocalBean
public interface IRepaiRequest {
	public RepairRequest createRepairRequest(RepairRequest repairRequest);

	public List<RepairRequest> getAllRepairRequest();

	public int updateRepairRequest(int id, RepairRequest reprequest);

	public boolean deleteRepairRequest(int id);

	public RepairRequest findRepairRequest(int id);

	public Invoice findInvoiceById(int id);

	public void RepairRequestTreatment(int id);

	public Product findProductByinvoice(int idp, int idinv);

	public int RepairRequestStatus(int id, RepairRequest t);

	public String findSentiment() throws JsonParseException, JsonMappingException, IOException;

	public String ReviewAdd(String r, int idRepReq);
}
