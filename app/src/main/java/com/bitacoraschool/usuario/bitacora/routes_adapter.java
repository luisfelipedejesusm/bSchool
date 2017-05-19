package com.bitacoraschool.usuario.bitacora;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class routes_adapter extends ArrayAdapter<rutas_model> {

    public routes_adapter(Activity context, int resource, List<rutas_model> objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_rutas, parent,false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        rutas_model CurrentRoute = getItem(position);

        TextView  localidad = (TextView) listItemView.findViewById(R.id.txtLocalidad);
        localidad.setText("Localidad: " + CurrentRoute.getLocalidadNombre());

        TextView tanda = (TextView) listItemView.findViewById(R.id.txtTanda);
        tanda.setText(CurrentRoute.getTanda());
        // Se obtiene la direccion del evento y se asigna a la lista
        TextView routeId = (TextView) listItemView.findViewById(R.id.txtRutaID);
        routeId.setText(String.valueOf(CurrentRoute.getDescripcion()));

        TextView type = (TextView) listItemView.findViewById(R.id.txttype);
        if (CurrentRoute.getRutaType()==1){
            type.setText("Ida");
        }else if(CurrentRoute.getRutaType()==2){
            type.setText("Vuelta");
        }


        return listItemView;
    }

}
