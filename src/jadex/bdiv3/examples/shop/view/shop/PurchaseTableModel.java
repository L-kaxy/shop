package jadex.bdiv3.examples.shop.view.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import jadex.bdiv3.examples.shop.entity.po.CommodityPo;

public class PurchaseTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected String[] tableHeader = { "名称", "批发价格", "计划售价", "自动采购线", "采购目标" };

		protected List<CommodityPo> list;
		protected Map<String, Double> purchaseMap;

		public PurchaseTableModel(List<CommodityPo> list, Map<String, Double> purchaseMap) {
			this.list = list;
			this.purchaseMap = purchaseMap;
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
				if (purchaseMap.containsKey(po.getCommodityname())) {
					value = Double.valueOf(purchaseMap.get(po.getCommodityname()));
				} else {
					value = "商品停供";
				}
			} else if (column == 2) {
				value = Double.valueOf(po.getSingleprice());
			} else if (column == 3) {
				value = Long.valueOf(po.getPurchase());
			} else if (column == 4) {
				value = Long.valueOf(po.getTotal());
			}
			return value;
		}
	}
