package org.example;

import java.util.List;

public class AccountingDocument {
    public String id;
    public String date;
    public String customerName;
    List<Account> accountList;
    public Double totalValueDe;
    public Double totalValueCr;

    public AccountingDocument(String date,String id, String customerName, List<Account> accountList) {
        this.id = id;
        this.date = date;
        this.customerName = customerName;
        this.accountList = accountList;
        for(Account account : accountList){
          if(account.getValueCre()!=null) {
              totalValueCr+=account.getValueCre();
          }
          if(account.getValueDe()!=null){
              totalValueDe+=account.getValueDe();
          }

        }
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<Account> getAccountList() {
        return accountList;
    }
    public Double getTotalValueDe() {
        return totalValueDe;
    }

    public Double getTotalValueCr() {
        return totalValueCr;
    }

}
