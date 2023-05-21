package com.squareup.shopx.viewholder;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.shopx.AllMerchants;
import com.squareup.shopx.R;
import com.squareup.shopx.adapter.CartItemListAdapter;
import com.squareup.shopx.model.AllMerchantsResponse;
import com.squareup.shopx.model.CartBottomSheetCallback;
import com.squareup.shopx.model.CartUpdateEvent;
import com.squareup.shopx.model.GetMerchantDetailResponse;
import com.squareup.shopx.widget.ItemBottomDialog;

import org.greenrobot.eventbus.EventBus;

public class CartItemViewHolder extends RecyclerView.ViewHolder {

    private Activity activity;
    private final ImageView itemImage;
    private final TextView count;
    private final TextView price;
    private final TextView itemName;
    private final LinearLayout addItem, subItem;

    public CartItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemImage = itemView.findViewById(R.id.item_image);
        count = itemView.findViewById(R.id.count);
        price = itemView.findViewById(R.id.price);
        itemName = itemView.findViewById(R.id.item_name);
        addItem = itemView.findViewById(R.id.add_item);
        subItem = itemView.findViewById(R.id.sub_item);
    }

    public void setData(GetMerchantDetailResponse.Item item, Activity activity, AllMerchantsResponse.ShopXMerchant merchantInfo, CartBottomSheetCallback listener, CartItemListAdapter adapter) {
        this.activity = activity;

        Glide.with(activity).load(item.getItemImage()).into(itemImage);
        itemName.setText(item.getItemName());
        price.setText("$" + String.format("%.2f", item.getItemDiscountPrice(merchantInfo) / 100.0));

        count.setText(String.valueOf(AllMerchants.INSTANCE.getCountOfAnItem(merchantInfo, item)));

        itemView.setOnClickListener(view -> {
            listener.dismissBottomSheet();
            showItemBottomSheet(item, merchantInfo);
        });

        addItem.setOnClickListener(view -> {
            int currentCount =  AllMerchants.INSTANCE.getCountOfAnItem(merchantInfo, item);
            AllMerchants.INSTANCE.updateItemNumber(merchantInfo, item, currentCount + 1);
            refreshCartInfo(adapter, listener);
            EventBus.getDefault().post(new CartUpdateEvent(merchantInfo));
        });

        subItem.setOnClickListener(view -> {
            int currentCount =  AllMerchants.INSTANCE.getCountOfAnItem(merchantInfo, item);
            AllMerchants.INSTANCE.updateItemNumber(merchantInfo, item, currentCount - 1);
            refreshCartInfo(adapter, listener);
            EventBus.getDefault().post(new CartUpdateEvent(merchantInfo));
        });
    }

    private void refreshCartInfo(CartItemListAdapter adapter, CartBottomSheetCallback listener) {
       adapter.refreshItems();
       listener.updatePrice();
    }

    private void showItemBottomSheet(GetMerchantDetailResponse.Item item, AllMerchantsResponse.ShopXMerchant merchantInfo) {
        ItemBottomDialog bottomSheetDialog = new ItemBottomDialog(activity, R.style.BottomSheetDialogStyle);
        bottomSheetDialog.init(activity, ItemBottomDialog.Companion.getFROM_CART(), item, merchantInfo);
    }
}
