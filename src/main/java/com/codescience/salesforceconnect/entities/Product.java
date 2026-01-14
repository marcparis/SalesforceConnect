package com.codescience.salesforceconnect.entities;

import java.math.BigDecimal;

/**
 * Implementation of the BaseEnity for each individual Insurance Product
 */
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String productName;

    private String productType;

    private BigDecimal costPerUnit;

    private boolean active;

    /**
     * Method returns the name of the Product
     * @return String that represents the Product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Method sets the name of the Product
     * @param productName String that represents the Product name
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Method returns the Product Type
     * @return String that represents the Product type
     */
    public String getProductType() {
        return productType;
    }

    /**
     * Method sets the Product Type
     * @param productType String that represents the Product type
     */
    public void setProductType(String productType) {
        this.productType = productType;
    }

    /**
     * Method returns the Cost per Insurance Unit
     * @return BigDecimal that represents the Cost per Insurance Unit
     */
    public BigDecimal getCostPerUnit() {
        return costPerUnit;
    }

    /**
     * Method sets the Cost per Insurance Unit
     * @param costPerUnit BigDecimal that represents the Cost per Insurance Unit
     */
    public void setCostPerUnit(BigDecimal costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    /**
     * Method returns true if the product is Active
     * @return true if the product is Active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Method sets true if the product is Active
     * @param active boolean set to true if the product is Active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Product product = (Product) o;

        if (isActive() != product.isActive()) return false;
        if (getProductName() != null ? !getProductName().equals(product.getProductName()) : product.getProductName() != null)
            return false;
        if (getProductType() != null ? !getProductType().equals(product.getProductType()) : product.getProductType() != null)
            return false;
        return getCostPerUnit() != null ? getCostPerUnit().equals(product.getCostPerUnit()) : product.getCostPerUnit() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getProductName() != null ? getProductName().hashCode() : 0);
        result = 31 * result + (getProductType() != null ? getProductType().hashCode() : 0);
        result = 31 * result + (getCostPerUnit() != null ? getCostPerUnit().hashCode() : 0);
        result = 31 * result + (isActive() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productName='" + productName + '\'' +
                ", productType='" + productType + '\'' +
                ", costPerUnit=" + costPerUnit +
                ", active=" + active +
                "} " + super.toString();
    }
}
