package jadex.bdiv3.examples.shop.view.customer;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import jadex.bdiv3.examples.shop.entity.view.ItemInfo;

class ItemTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected List<?> list;

	public ItemTableModel(List<?> list) {
		this.list = list;
	}

	public int getRowCount() {
		return list.size();
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "商品";
		case 1:
			return "单价";
		case 2:
			return "数量";
		default:
			return "";
		}
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public Object getValueAt(int row, int column) {
		Object value = null;
		ItemInfo ii = (ItemInfo) list.get(row);
		if (column == 0) {
			value = ii.getName();
		} else if (column == 1) {
			value = Double.valueOf(ii.getPrice());
		} else if (column == 2) {
			value = Long.valueOf(ii.getQuantity());
		}
		return value;
	}

}
