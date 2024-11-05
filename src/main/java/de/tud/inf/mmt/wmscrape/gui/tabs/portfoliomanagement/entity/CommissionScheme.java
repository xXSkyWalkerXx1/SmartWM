package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity;

import de.tud.inf.mmt.wmscrape.gui.tabs.historic.data.SecuritiesType;
import org.apache.xmlbeans.impl.store.Cur;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Entity
@Table(name = "provisions_schema")
public class CommissionScheme {

    // region entities as inner-classes
    @Entity
    @Table(name = "depot_preis_staffel")
    public static class DepotPriceScaling {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "depot_value_from", nullable = false)
        private int depotValueFrom;

        @Column(name = "depot_value_to", nullable = false)
        private int depotValueTo;

        @Column(name = "min_price", nullable = false)
        private double minPrice; // in %

        public Long getId() {
            return id;
        }

        public int getDepotValueFrom() {
            return depotValueFrom;
        }

        public void setDepotValueFrom(int depotValueFrom) {
            this.depotValueFrom = depotValueFrom;
        }

        public int getDepotValueTo() {
            return depotValueTo;
        }

        public void setDepotValueTo(int depotValueTo) {
            this.depotValueTo = depotValueTo;
        }

        public double getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(double minPrice) {
            this.minPrice = minPrice;
        }
    }

    @Entity
    @Table(name = "bank_gebühren")
    public static class BankFee {

        @Entity
        @Table(name = "bank_gebühren_wertpapier")
        static class SecuritiesTypeSpecificBankFee {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

            @Enumerated(EnumType.STRING)
            @Column(name = "securities_type", nullable = false)
            private SecuritiesType securitiesType;

            @Column(name = "fee", nullable = false)
            private double fee;

            public Long getId() {
                return id;
            }

            public SecuritiesType getSecuritiesType() {
                return securitiesType;
            }

            public void setSecuritiesType(SecuritiesType securitiesType) {
                this.securitiesType = securitiesType;
            }

            public double getFee() {
                return fee;
            }

            public void setFee(double fee) {
                this.fee = fee;
            }
        }

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "order_volume_from", nullable = false)
        private int orderVolumeFrom;

        @Column(name = "order_volume_to", nullable = false)
        private int orderVolumeTo;

        @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
        @JoinColumn(name = "securities_type_bank_fee_id")
        private List<SecuritiesTypeSpecificBankFee> securitiesTypeSpecificBankFees = new ArrayList<>();

        @Column(name = "minimum_price_bank_advisor", nullable = false)
        private double minPriceBankAdvisor;

        @Column(name = "minimum_price_online", nullable = false)
        private double minPriceOnline;

        @Column(name = "maximum_price_bank_advisor", nullable = false)
        private double maxPriceBankAdvisor;

        @Column(name = "maximum_price_online", nullable = false)
        private double maxPriceOnline;

        public Long getId() {
            return id;
        }

        public int getOrderVolumeFrom() {
            return orderVolumeFrom;
        }

        public void setOrderVolumeFrom(int orderVolumeFrom) {
            this.orderVolumeFrom = orderVolumeFrom;
        }

        public int getOrderVolumeTo() {
            return orderVolumeTo;
        }

        public void setOrderVolumeTo(int orderVolumeTo) {
            this.orderVolumeTo = orderVolumeTo;
        }

        public List<SecuritiesTypeSpecificBankFee> getSecuritiesTypeSpecificBankFees() {
            return securitiesTypeSpecificBankFees;
        }

        public void setSecuritiesTypeSpecificBankFees(List<SecuritiesTypeSpecificBankFee> securitiesTypeSpecificBankFees) {
            this.securitiesTypeSpecificBankFees = securitiesTypeSpecificBankFees;
        }

        public double getMinPriceBankAdvisor() {
            return minPriceBankAdvisor;
        }

        public void setMinPriceBankAdvisor(double minPriceBankAdvisor) {
            this.minPriceBankAdvisor = minPriceBankAdvisor;
        }

        public double getMinPriceOnline() {
            return minPriceOnline;
        }

        public void setMinPriceOnline(double minPriceOnline) {
            this.minPriceOnline = minPriceOnline;
        }

        public double getMaxPriceBankAdvisor() {
            return maxPriceBankAdvisor;
        }

        public void setMaxPriceBankAdvisor(double maxPriceBankAdvisor) {
            this.maxPriceBankAdvisor = maxPriceBankAdvisor;
        }

        public double getMaxPriceOnline() {
            return maxPriceOnline;
        }

        public void setMaxPriceOnline(double maxPriceOnline) {
            this.maxPriceOnline = maxPriceOnline;
        }
    }
    // endregion

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "depot_price_scaling_id", nullable = false)
    private List<DepotPriceScaling> depotPriceScalings = new ArrayList<>(); // Depot-Preisstaffel

    @Column(name = "exchange_fee", nullable = false)
    private double exchangeFee; // Börsengebühr

    @Column(name = "brokerage_fee", nullable = false)
    private double brokerageFee; // Maklergebühren

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_fee_id", nullable = false)
    private List<BankFee> bankFees = new ArrayList<>();

    // region Getters & Setters
    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Currency getCurrencyCode() {
        return Currency.getInstance(currencyCode);
    }

    public void setCurrencyCode(Currency currency) {
        this.currencyCode = currency.getCurrencyCode();
    }

    public List<DepotPriceScaling> getDepotPriceScalings() {
        return depotPriceScalings;
    }

    public void setDepotPriceScalings(List<DepotPriceScaling> depotPriceScalings) {
        this.depotPriceScalings = depotPriceScalings;
    }

    public double getExchangeFee() {
        return exchangeFee;
    }

    public void setExchangeFee(double exchangeFee) {
        this.exchangeFee = exchangeFee;
    }

    public double getBrokerageFee() {
        return brokerageFee;
    }

    public void setBrokerageFee(double brokerageFee) {
        this.brokerageFee = brokerageFee;
    }

    public List<BankFee> getBankFees() {
        return bankFees;
    }

    public void setBankFees(List<BankFee> bankFees) {
        this.bankFees = bankFees;
    }
    // endregion
}
