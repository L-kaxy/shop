/**
 * Copyright (c) 2007-2016 Wteam.  All rights reserved. 网维网络技术创业团队 版权所有.
 * 请勿修改或删除版权声明及文件头部.
 */
package jadex.bdiv3.examples.shop.view.customer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * @author 罗佳欣
 *
 */
class RechargeJDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public double value = 0;
	public boolean isOk = false;
	JTextField moneyText;
	JButton ok;
	JButton cancel;

	public RechargeJDialog(JFrame parent) {
		super(parent, true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("充值");
		setSize(300, 120);
		setLocationRelativeTo(parent);
		setResizable(false);
		setLayout(null);
		moneyText = new JTextField(30);
		ok = new JButton("充值");
		cancel = new JButton("取消");
		JLabel Text1 = new JLabel("充入金额:");
		add(Text1);
		add(moneyText);
		add(ok);
		add(cancel);
		Text1.setBounds(40, 25, 60, 25);
		moneyText.setBounds(110, 25, 120, 25);
		ok.setBounds(60, 60, 80, 25);
		cancel.setBounds(160, 60, 80, 25);

		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tMoney = moneyText.getText().trim();
				if (tMoney.replaceAll("[0.0-9.0]", "").length() != 0 || tMoney.equals("")
						|| moneyText.getText().split("\\.").length > 2 || moneyText.getText().endsWith(".")) {
					JOptionPane.showMessageDialog(null, "输入金额中含有非法字符或为空", "错误信息", JOptionPane.ERROR_MESSAGE);
					return;
				}
				value = Double.valueOf(moneyText.getText());
				isOk = true;
				dispose();
			}
		});

		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		setVisible(true);
	}

}