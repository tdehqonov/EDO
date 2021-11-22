package uz.sicnt.eimzo.holder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;

import uz.sicnt.eimzo.R;

/**
 * Created by Bogdan Melnychuk on 2/15/15.
 */
public class SelectableItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private CheckBox nodeSelector;

    public SelectableItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItemHolder.IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_selectable_item, null, false);

        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        final PrintView iconView = view.findViewById(R.id.printviewid);
        iconView.setIconText(context.getResources().getString(value.icon));

        if (value.color != "") {
            iconView.setIconColor(Color.parseColor(value.color));
              switch (value.color){
                  case "#12558C":
                      tvValue.setBackgroundColor(Color.parseColor("#F1F1F1"));
                      break;
                  case "#FF1100":
                      tvValue.setBackgroundColor(Color.parseColor("#FADCDC"));
                      break;
                  case "#4DAA51":
                      tvValue.setBackgroundColor(Color.parseColor("#DDF8DE"));
                      break;
                  case "#2196F3":
                      tvValue.setBackgroundColor(Color.parseColor("#CDE2F3"));
                      break;
                  case "#FFEB3B":
                      tvValue.setBackgroundColor(Color.parseColor("#FFFCE0"));
                      break;

              }

        }

        nodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
        nodeSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                node.setSelected(isChecked);
            }
        });
        nodeSelector.setChecked(node.isSelected());

        if (node.isLastChild()) {
            view.findViewById(R.id.bot_line).setVisibility(View.INVISIBLE);
        }

        return view;
    }


    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }

    public static class IconTreeItem {
        public int icon;
        public String text;

        public IconTreeItem(int icon, String text) {
            this.icon = icon;
            this.text = text;
        }
    }
}
