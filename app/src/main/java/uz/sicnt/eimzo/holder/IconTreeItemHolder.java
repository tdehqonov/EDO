package uz.sicnt.eimzo.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;

import uz.sicnt.eimzo.R;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private PrintView arrowView;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);


        final View view = inflater.inflate(R.layout.layout_icon_node, null, false);

        tvValue = view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        final PrintView iconView = view.findViewById(R.id.icon);
        iconView.setIconText(context.getResources().getString(value.icon));

        arrowView = view.findViewById(R.id.arrow_icon);

        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    public static class IconTreeItem {
        public int icon;
        public String text;
        public String fileid;
        public String color;

        public IconTreeItem(int icon, String text, String fileid, String color) {
            this.icon = icon;
            this.text = text;
            this.fileid = fileid;
            this.color = color;
        }
    }
}
