package com.egco.storefinder.model;

import java.io.Serializable;

public class ProductModel implements Serializable,Comparable<ProductModel>{

	private static final long serialVersionUID = 1588383123523114312L;
	private int productID;
	private String productImgURL;
	private String ownerImgURL;
	private double price;
	private double discount;
	private double shippingCost;
	private long popularity;
	private String productName;
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getProductID() {
		return productID;
	}
	public void setProductID(int productID) {
		this.productID = productID;
	}
	public String getProductImgURL() {
		return productImgURL;
	}
	public void setProductImgURL(String productImgURL) {
		this.productImgURL = productImgURL;
	}
	public String getOwnerImgURL() {
		return ownerImgURL;
	}
	public void setOwnerImgURL(String ownerImgURL) {
		this.ownerImgURL = ownerImgURL;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public double getShippingCost() {
		return shippingCost;
	}
	public void setShippingCost(double shippingCost) {
		this.shippingCost = shippingCost;
	}
	public long getPopularity() {
		return popularity;
	}
	public void setPopularity(long popularity) {
		this.popularity = popularity;
	}
	@Override
	public int compareTo(ProductModel another) {
		if(this.popularity > another.popularity) {
			return -1;
		} else if(this.popularity == another.popularity) {
			return 0;
		} else {
			return 1;
		}
	}
	
	

}
