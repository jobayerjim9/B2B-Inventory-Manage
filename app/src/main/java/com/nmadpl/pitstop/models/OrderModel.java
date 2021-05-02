package com.nmadpl.pitstop.models;

import java.io.Serializable;
import java.util.ArrayList;

import static com.nmadpl.pitstop.models.Constants.ORDER_PENDING;

public class OrderModel implements Serializable {
    private String orderId,orderDate;
    private double orderTotal;
    private String orderBy,status,acceptedDate,deliveredDate,transportName;
    private ArrayList<OrderItem> orderItems=new ArrayList<>();

    public OrderModel() {
        transportName="";
        status=ORDER_PENDING;

    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public String getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(String acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public String getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(String deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }




    public static class OrderItem implements Serializable {
        private String itemId,itemName,mfgCode;
        private int qty;
        private double itemTotalTotal;

        public OrderItem() {
        }

        public OrderItem(String itemId, String itemName,String mfgCode, int qty, double itemTotalTotal) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.qty = qty;
            this.itemTotalTotal = itemTotalTotal;
        }

        public String getMfgCode() {
            return mfgCode;
        }

        public void setMfgCode(String mfgCode) {
            this.mfgCode = mfgCode;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public double getItemTotalTotal() {
            return itemTotalTotal;
        }

        public void setItemTotalTotal(double itemTotalTotal) {
            this.itemTotalTotal = itemTotalTotal;
        }
    }
}
