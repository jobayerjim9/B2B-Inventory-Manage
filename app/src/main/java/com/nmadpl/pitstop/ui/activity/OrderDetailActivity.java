package com.nmadpl.pitstop.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.OrderItemAdapter;
import com.nmadpl.pitstop.controllers.helpers.FileUtils;
import com.nmadpl.pitstop.databinding.ActivityOrderDetailBinding;
import com.nmadpl.pitstop.models.Constants;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.OrderModel;
import com.nmadpl.pitstop.models.UserDetail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class OrderDetailActivity extends AppCompatActivity {
    ActivityOrderDetailBinding binding;
    final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail);
        initView();
    }

    private void initView() {
        OrderModel orderModel = (OrderModel) getIntent().getSerializableExtra("item");
        binding.setData(orderModel);
        binding.orderItemRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.orderItemRecycler.setAdapter(new OrderItemAdapter(this,orderModel.getOrderItems()));
        binding.changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(OrderDetailActivity.this,v);
                popupMenu.getMenuInflater().inflate(R.menu.status_change_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Date now=new Date();
                        String placeHolder=now.getDate()+"-"+(now.getMonth()+1)+"-"+(now.getYear()+1900);
                        if (item.getItemId()==R.id.acceptedStatus) {
                            orderModel.setAcceptedDate(placeHolder);
                            updateOrder(orderModel, Constants.ORDER_ACCEPTED);
                        }
                        else if (item.getItemId()==R.id.pendingStatus) {
                            updateOrder(orderModel, Constants.ORDER_PENDING);
                        } else if (item.getItemId() == R.id.deliveredStatus) {
                            orderModel.setDeliveredDate(placeHolder);
                            updateOrder(orderModel, Constants.ORDER_DELIVERED);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        binding.createInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(OrderDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(OrderDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(OrderDetailActivity.this, permissions, 1);
                } else {
                    createInvoice();
                }

            }
        });
        binding.setLoading(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.USER_PATH).child(orderModel.getOrderBy());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.setLoading(false);
                UserDetail userDetail = snapshot.getValue(UserDetail.class);
                if (userDetail != null) {
                    binding.setUser(userDetail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                createInvoice();
            } else {
                ActivityCompat.requestPermissions(OrderDetailActivity.this, permissions, 1);
            }
        }
    }

    private void createInvoice() {
        OrderModel orderModel = binding.getData();
        UserDetail userDetail = binding.getUser();

        String dest = FileUtils.getAppPath(this) + orderModel.getOrderId() + ".pdf";
        try {
            PdfWriter pdfWriter = null;
            pdfWriter = new PdfWriter(new FileOutputStream(dest));
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            PdfFont normal = PdfFontFactory.createFont("assets/fonts/times_new_roman.ttf", "UTF-8", true);
            // PdfFont medium = PdfFontFactory.createFont("assets/fonts/brandon_medium.otf", "UTF-8", true);
            PdfFont bold = PdfFontFactory.createFont("assets/fonts/times_new_roman_bold.ttf", "UTF-8", true);
            Document document = new Document(pdfDocument, PageSize.A4, true);
            Paragraph head = new Paragraph("Invoice By National");
            head.setFont(bold);
            head.setFontSize(32);
            head.setTextAlignment(TextAlignment.CENTER);
            document.add(head);
            Paragraph to = new Paragraph("To,");
            to.setFont(bold);
            to.setFontSize(32);
            to.setUnderline();
            document.add(to);
            Paragraph name = new Paragraph(userDetail.getFullName() + ", " + userDetail.getFirmName());
            name.setFont(bold);
            name.setFontSize(32);
            name.setUnderline();
            document.add(name);
            Text ph = new Text("Ph: " + userDetail.getPhone());
            ph.setFont(bold);
            ph.setFontSize(32);
            ph.setUnderline();
            Paragraph phone = new Paragraph(ph);
            phone.add(new Tab());
            phone.addTabStops(new TabStop(1000, TabAlignment.RIGHT));
            Text date = new Text("Order Date: " + orderModel.getOrderDate());
            date.setFont(normal);
            date.setFontSize(16);
            phone.add(date);
            document.add(phone);
            Paragraph transport = new Paragraph("Transport: " + orderModel.getTransportName());
            transport.setFont(bold);
            transport.setFontSize(32);
            transport.setUnderline();
            document.add(transport);
            document.add(new Paragraph(""));
            Paragraph details = new Paragraph("Order Details");
            details.setTextAlignment(TextAlignment.CENTER);
            details.setFont(bold);
            details.setFontSize(32);
            document.add(details);
            float[] pointColumnWidths = {20F, 200F, 100F, 100F, 100F, 100F, 100F};
            Table table = new Table(pointColumnWidths);
            table.setTextAlignment(TextAlignment.CENTER);
            table.setFont(normal);
            table.setFontSize(14);
            table.addCell("SN.");
            table.addCell("Description");
            table.addCell("Price");
            table.addCell("Rate");
            table.addCell("Units");
            table.addCell("Qty");
            table.addCell("Amount");
            for (int i = 0; i < orderModel.getOrderItems().size(); i++) {
                final OrderModel.OrderItem orderItem = orderModel.getOrderItems().get(i);
                table.addCell((i + 1) + "");
                table.addCell(orderItem.getMfgCode() + " " + orderItem.getItemName());
                table.addCell(((orderItem.getItemTotalTotal()) / orderItem.getQty()) + "");
                table.addCell(((orderItem.getItemTotalTotal()) / orderItem.getQty()) + "");
                table.addCell(orderItem.getUnit());
                table.addCell(orderItem.getQty() + "");
                table.addCell(orderItem.getItemTotalTotal() + "");
            }
            document.add(table);
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));

            document.add(new LineSeparator(new DashedLine()));
            Text total = new Text("Total:");
            total.setFont(bold);
            total.setFontSize(32);
            Paragraph totalP = new Paragraph(total);
            totalP.add(new Tab());
            totalP.addTabStops(new TabStop(1000, TabAlignment.RIGHT));
            Text amount = new Text(orderModel.getOrderTotal() + "");
            amount.setFont(bold);
            amount.setFontSize(32);
            totalP.add(amount);
            document.add(totalP);
            document.add(new LineSeparator(new DashedLine()));


            document.close();
            FileUtils.openFile(this, new File(dest));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateOrder(OrderModel orderModel, String order) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.ORDER_PATH).child(orderModel.getOrderBy()).child(orderModel.getOrderId());
        orderModel.setStatus(order);
        databaseReference.setValue(orderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(OrderDetailActivity.this, "Status Updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}