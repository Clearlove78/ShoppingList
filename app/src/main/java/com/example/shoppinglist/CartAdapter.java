package com.example.shoppinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CartAdapter extends BaseAdapter {

    private Context context;
    private List<HashMap<String,String>> dataList;
    private ViewHolder vh;
    private LayoutInflater inflater;
    private Map<String,Integer> pitchOnMap;
    private RefreshPriceInterface refreshPriceInterface;

    public CartAdapter(Context context, List<HashMap<String,String>> list){
        this.context=context;
        this.dataList=list;

        pitchOnMap=new HashMap<>();
        for(int i=0;i<dataList.size();i++){
            pitchOnMap.put(dataList.get(i).get("id"),0);
        }
        inflater=LayoutInflater.from(this.context);
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder=null;
        if(view==null){
            vh=new ViewHolder();
            view= LayoutInflater.from(context).inflate(R.layout.item_shopcart_product,null);
            vh.icon=(ImageView)view.findViewById(R.id.iv_adapter_list_pic);
            vh.name=(TextView)view.findViewById(R.id.tv_goods_name);
            vh.price=(TextView)view.findViewById(R.id.tv_goods_price);
            vh.reduce=(TextView)view.findViewById(R.id.tv_reduce);
            vh.type=(TextView)view.findViewById(R.id.tv_type_size);
            vh.num=(TextView)view.findViewById(R.id.tv_num);
            vh.add=(TextView)view.findViewById(R.id.tv_add);
            vh.mytext=(TextView) view.findViewById(R.id.mytext);
            view.setTag(vh);
        }else {
            vh=(ViewHolder)view.getTag();
        }

        if(dataList.size()>0){
            HashMap<String,String> map=dataList.get(position);
            vh.name.setText(map.get("name"));
            vh.num.setText(map.get("count"));
            vh.type.setText(map.get("type"));
            vh.price.setText("$ "+(Double.valueOf(map.get("price")) * Integer.valueOf(map.get("count"))));
            vh.reduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int index=position;
                    dataList.get(index).put("count",(Integer.valueOf(dataList.get(index).get("count"))-1)+"");
                    if(Integer.valueOf(dataList.get(index).get("count"))<=0){
                        String deID=dataList.get(index).get("id");
                        dataList.remove(index);
                        pitchOnMap.remove(deID);
                    }
                    notifyDataSetChanged();
                    refreshPriceInterface.refreshPrice(pitchOnMap);
                }
            });
            vh.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int index=position;
                    dataList.get(index).put("count",(Integer.valueOf(dataList.get(index).get("count"))+1)+"");
                    if(Integer.valueOf(dataList.get(index).get("count"))>15){
                        //Max 15
                        Toast.makeText(context,"Max",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    notifyDataSetChanged();
                    refreshPriceInterface.refreshPrice(pitchOnMap);
                }
            });
        }

        return view;
    }

    public Map<String,Integer> getPitchOnMap(){
        return pitchOnMap;
    }
    public void setPitchOnMap(Map<String,Integer> pitchOnMap){
        this.pitchOnMap=new HashMap<>();
        this.pitchOnMap.putAll(pitchOnMap);
    }

    public interface RefreshPriceInterface{
        void refreshPrice(Map<String,Integer> pitchOnMap);
    }
    public void setRefreshPriceInterface(RefreshPriceInterface refreshPriceInterface){
        this.refreshPriceInterface=refreshPriceInterface;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getCount() {
        if (dataList != null) {
            return dataList.size();
        } else {
            return 0;
        }
    }

    class ViewHolder{
        ImageView icon;
        TextView mytext,name,price,num,type,reduce,add;
    }

}
