package root;

import com.stripe.Stripe;
import com.stripe.model.Account;
import com.stripe.model.Charge;
import com.stripe.model.Customer;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws Exception {
        Stripe.apiKey = "xxx";

        Account doctor = createManagedAccountForDoctor("xxx@mail.ru", "4000056655665556", "2020", "12", "121", "Dr. Alex");
        System.out.println(doctor);

        Customer payer = createCustomer("yyy@gmail.com", null);
        System.out.println(payer);

        Charge charge = charge(payer, doctor, 1100 /* 11.00 $ */);
        System.out.println(charge);
    }

    private static Account createManagedAccountForDoctor(final String email,
                                                         final String number,
                                                         final String expYear,
                                                         final String expMonth,
                                                         final String cvc,
                                                         final String businessName) throws Exception {
        return Account.create(new HashMap<String, Object>() {
            {
                put("country", "US");
                put("email", email);
                put("managed", true);
                put("business_name", businessName);
                put("external_account", new HashMap<String, String>() {
                    {
                        put("object", "card");
                        put("number", number);
                        put("exp_year", expYear);
                        put("exp_month", expMonth);
                        put("cvc", cvc);
                        put("currency", "usd");
                    }
                });
            }
        });
    }

    private static Charge charge(final Customer customerFrom, final Account accountTo, final long amount) throws Exception {
        return Charge.create(new HashMap<String, Object>() {
            {
                put("amount", amount);
                put("currency", "usd");
                put("destination", accountTo.getId());
                put("description", "From " + customerFrom.getEmail() + " to " + accountTo.getEmail() + " amount " + amount + " pennies");
                put("customer", customerFrom.getId());
            }
        });
    }

    private static Customer createCustomer(final String email, final Object source) throws Exception {
        return Customer.create(new HashMap<String, Object>() {
            {
                put("email", email);
                put("description", "Customer " + email);
                put("source", source == null ? new HashMap<String, String>() {
                    {
                        put("object", "card");
                        put("exp_month", "11");
                        put("exp_year", "2020");
                        put("number", "4242 4242 4242 4242");
                        put("cvc", "121");
                    }
                } : source); // or obtained with Stripe.js
            }
        });
    }
}
