package jadex.bdiv3.examples.shop.view.shop;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import jadex.bdiv3.examples.shop.entity.po.CommodityPo;

public class CommodityTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String[] tableHeader = { "名称", "价格", "数量", "自动采购线", "采购目标"};

	protected List<CommodityPo> list;

	public CommodityTableModel(List<CommodityPo> list) {
		this.list = list;
		if (this.list == null) {
			this.list = new ArrayList<>();
		}
	}

	public int getRowCount() {
		return list.size();
	}

	public int getColumnCount() {
		return tableHeader.length;
	}

	public String getColumnName(int column) {
		return tableHeader[column];
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public Object getValueAt(int row, int column) {
		Object value = null;
		CommodityPo po = (CommodityPo) list.get(row);
		if (column == 0) {
			value = po.getCommodityname();
		} else if (column == 1) {
			value = Double.valueOf(po.getSingleprice());
		} else if (column == 2) {
			value = Long.valueOf(po.getQuantity());
		} else if (column == 3) {
			value = Long.valueOf(po.getPurchase());
		} else if (column == 4) {
			value = Long.valueOf(po.getTotal());
		}
		return value;
	}
};