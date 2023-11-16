package lk.ijse.dep11.db;

import lk.ijse.dep11.tm.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDataAccessTest {

    @Test
    void sqlSyntax() {
        assertDoesNotThrow(()-> Class.forName("lk.ijse.dep11.db.CustomerDataAccess"));
    }

    @BeforeEach
    void setUp() throws SQLException {
        SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(false);
    }

    @AfterEach
    void tearDown() throws SQLException {
        SingleConnectionDataSource.getInstance().getConnection().rollback();
        SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(true);
    }

    @Test
    void saveCustomer() {
        assertDoesNotThrow(()->{
            CustomerDataAccess.saveCustomer(new Customer("ABC", "Kasun", "Galle","011-12345356"));
            CustomerDataAccess.saveCustomer(new Customer("EDF", "Ruwan", "Galle","011-1234242456"));
        });
        assertThrows(SQLException.class, ()-> CustomerDataAccess
                .saveCustomer(new Customer("ABC", "Kasun", "Galle","011-123456")));
    }

    @Test
    void getAllCustomers() throws SQLException {
        CustomerDataAccess.saveCustomer(new Customer("ABC", "Kasun", "Galle","011-123445256"));
        CustomerDataAccess.saveCustomer(new Customer("EDF", "Ruwan", "Galle","011-1256"));
        assertDoesNotThrow(()->{
            List<Customer> customerList = CustomerDataAccess.getAllCustomers();
            assertTrue(customerList.size() >= 2);
        });
    }

    @Test
    void updateCustomer() throws SQLException {
        CustomerDataAccess.saveCustomer(new Customer("ABC", "Kasun", "Galle","011-3456"));
        assertDoesNotThrow(()-> CustomerDataAccess
                .updateCustomer(new Customer("ABC", "Ruwan", "Matara","011-156")));
    }

    @Test
    void deleteCustomer() throws SQLException {
        CustomerDataAccess.saveCustomer(new Customer("ABC", "Kasun", "Galle","0113456"));
        int size = CustomerDataAccess.getAllCustomers().size();
        assertDoesNotThrow(()-> {
            CustomerDataAccess.deleteCustomer("ABC");
            assertEquals(size - 1, CustomerDataAccess.getAllCustomers().size());
        });
    }

    @Test
    void getLastCustomerId() throws SQLException {
        String lastCustomerId = CustomerDataAccess.getLastCustomerId();
        if (CustomerDataAccess.getAllCustomers().isEmpty()){
            assertNull(lastCustomerId);
        }else{
            CustomerDataAccess.saveCustomer(new Customer("ABC", "Kasun", "Galle","011-1232456"));
            lastCustomerId = CustomerDataAccess.getLastCustomerId();
            System.out.println(lastCustomerId);
            CustomerDataAccess.saveCustomer(new Customer("ABdfC", "Kasun", "Galle","011-15382423456"));
            lastCustomerId = CustomerDataAccess.getLastCustomerId();
            System.out.println(lastCustomerId);
            assertNotNull(lastCustomerId);
        }
    }
}