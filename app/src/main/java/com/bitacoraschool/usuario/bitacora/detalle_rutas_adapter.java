package com.bitacoraschool.usuario.bitacora;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Octagono on 24/04/2017.
 */

public class detalle_rutas_adapter extends ArrayAdapter<detalleRuta_model>{

    private ArrayList<detalleRuta_model> mOriginalValues;
    private ArrayList<detalleRuta_model> mDisplayedValues;
    LayoutInflater inflater;


    public detalle_rutas_adapter(Activity context, int resource, ArrayList<detalleRuta_model> objects) {
        super(context,0, objects);
        this.mOriginalValues = objects;
        this.mDisplayedValues = objects;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                mDisplayedValues = (ArrayList<detalleRuta_model>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<detalleRuta_model> FilteredArrList = new ArrayList<detalleRuta_model>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<detalleRuta_model>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).getNombre();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new detalleRuta_model(mOriginalValues.get(i).getId(),mOriginalValues.get(i).getNombre(),mOriginalValues.get(i).getRutaID()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }


    @Override
    public int getCount() {
        return mDisplayedValues.size();
    }

    @Override
    public detalleRuta_model getItem(int position) {
        return mDisplayedValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        LinearLayout llContainer;
        TextView tvName,tvPrice;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_detalleruta, parent,false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        detalleRuta_model CurrentRoute = getItem(position);

        TextView localidad = (TextView) listItemView.findViewById(R.id.txtRutaID);
        localidad.setText("Ruta: " + CurrentRoute.getNombreRuta());

        if(CurrentRoute.getEstatus()==0){
            listItemView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.Red_Crimson2));
        }else if(CurrentRoute.getEstatus()==1){
            listItemView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.Green_LimeGreen50));
        }else if(CurrentRoute.getEstatus()==2){
            listItemView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.MediumAquamarine50));
            //listItemView.setEnabled(false);
        }


        TextView tanda = (TextView) listItemView.findViewById(R.id.txtNombre);
        tanda.setText(CurrentRoute.getNombre());


        return listItemView;
    }

}
