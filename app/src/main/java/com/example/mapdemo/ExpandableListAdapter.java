package com.example.mapdemo;

/**
 * Created by olsontl on 5/5/15.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private List<String> bus_routes;

    private ArrayList<boolean[]> checkedChildren;

    public ExpandableListAdapter(Activity context, final List<String> bus_routes) {
        this.context = context;
        this.bus_routes = bus_routes;

        checkedChildren = new ArrayList<boolean[]>() {{
            for (int i = 0; i < bus_routes.size(); i++) {
                add(new boolean[2]);
            }
        }};

    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String routeName = (String) getGroup(groupPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        CheckBox west = (CheckBox) convertView.findViewById(R.id.checkBoxWest);
        CheckBox east = (CheckBox) convertView.findViewById(R.id.checkBoxEast);

        west.setText(routeName.substring(4) + " " + context.getResources().getString(R.string.west));
        east.setText(routeName.substring(4) + " " + context.getResources().getString(R.string.east));
        west.setChecked(checkedChildren.get(groupPosition)[0]);
        east.setChecked(checkedChildren.get(groupPosition)[1]);

        west.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                MapActivity.mapHandler.showRoutes(cb.getText().toString(), 0, cb.isChecked());
                checkedChildren.get(groupPosition)[0] = cb.isChecked();
            }
        });
        east.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                MapActivity.mapHandler.showRoutes(cb.getText().toString(), 1, cb.isChecked());
                checkedChildren.get(groupPosition)[1] = cb.isChecked();
            }
        });

        return convertView;
    }

    public Object getGroup(int groupPosition) {
        return bus_routes.get(groupPosition);
    }

    public int getGroupCount() {
        return bus_routes.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String busRouteName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.bus_route_item,
                  null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.bus_route);
        item.setText(busRouteName);
        /*if (convertView.getBackground() == null) {
            MapResources mapResources = MapResources.getInstance();
            int routeNumber = mapResources.getRouteNumberByName(busRouteName);
            convertView.setBackgroundColor(mapResources.getColorByIndex(routeNumber));
        }*/
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}