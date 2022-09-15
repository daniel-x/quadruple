package de.a0h.quadruple.gui.desktop;

import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

import de.a0h.quadruple.gui.common.GuiState.PlayerType;

@SuppressWarnings("serial")
public class PlayerPanel extends Panel {

	Label ordinalLbl = new Label();
	StoneColorView colorVw = new StoneColorView();
	Button typeBtn = new Button(PlayerType.HUMAN.toString());
	TextField timeTf = new TextField("00:00:00.000");

	public PlayerPanel(boolean left) {
		timeTf.setEditable(false);

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		int gridXBase = left ? 0 : 1;

		ordinalLbl.setAlignment(left ? Label.LEFT : Label.RIGHT);

		add(ordinalLbl, this, gbc, gridXBase, 0, 1, 1, 0, 0, GridBagConstraints.BOTH);
		add(colorVw, this, gbc, gridXBase + 1, 0, 1, 1, 0, 0, GridBagConstraints.BOTH);
		add(typeBtn, this, gbc, gridXBase, 1, 2, 1, 0, 0, GridBagConstraints.BOTH);
		add(timeTf, this, gbc, gridXBase, 2, 2, 1, 0, 0, GridBagConstraints.BOTH);
	}

	private static void add(Component cmp, Container cnt, GridBagConstraints gbc, //
			int gridx, int gridy, int gridwidth, int gridheight, int weightx, int weighty, int fill) {
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.fill = fill;

		cnt.add(cmp, gbc);
	}
}
