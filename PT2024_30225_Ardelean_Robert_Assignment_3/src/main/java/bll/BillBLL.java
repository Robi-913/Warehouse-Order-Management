package bll;

import dao.BillDAO;
import model.Bill;

import java.util.List;

/**
 *
 */
public class BillBLL {

    private final BillDAO billDAO;


    public BillBLL() {
        billDAO = new BillDAO();
    }

    public Bill findBillById(int id) {
        return billDAO.findById(id);
    }

    public List<Bill> findAllBills() {
        return billDAO.findAll();
    }

    public void crateBill(Bill bill) {
        billDAO.insert(bill);
    }


}
