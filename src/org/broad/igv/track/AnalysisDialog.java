/*
 * Copyright (c) 2007-2012 The Broad Institute, Inc.
 * SOFTWARE COPYRIGHT NOTICE
 * This software and its documentation are the copyright of the Broad Institute, Inc. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. The Broad Institute is not responsible for its use, misuse, or functionality.
 *
 * This software is licensed under the terms of the GNU Lesser General Public License (LGPL),
 * Version 2.1 which is available at http://www.opensource.org/licenses/lgpl-2.1.php.
 */

/*
 * Created by JFormDesigner on Wed May 16 16:09:07 EDT 2012
 */

package org.broad.igv.track;

import org.broad.igv.ui.IGV;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author User #2
 */
public class AnalysisDialog extends JDialog {


    public AnalysisDialog(Frame owner) {
        super(owner);
        initComponents();

        operation.setModel(new DefaultComboBoxModel(CombinedFeatureSource.Operation.values()));
        track1Box.setModel(new DefaultComboBoxModel(getFeatureTracks(IGV.getInstance().getAllTracks(true)).toArray()));
        track2Box.setModel(new DefaultComboBoxModel(getFeatureTracks(IGV.getInstance().getAllTracks(true)).toArray()));
        track1Box.setRenderer(new TrackComboBoxRenderer());
        track2Box.setRenderer(new TrackComboBoxRenderer());

        ItemListener listener = new SetOutputTrackNameListener();
        track1Box.addItemListener(listener);
        track2Box.addItemListener(listener);
        operation.addItemListener(listener);

        setOutputTrackName();

    }

    private List<FeatureTrack> getFeatureTracks(List<Track> tracks) {

        List<FeatureTrack> featureTracks = new ArrayList<FeatureTrack>();
        for (Track t : tracks) {
            if (t instanceof FeatureTrack) {
                featureTracks.add((FeatureTrack) t);
            }
        }
        return featureTracks;
    }

    public AnalysisDialog(Frame owner, Iterator<Track> tracks) {
        this(owner);

        track1Box.setSelectedItem(tracks.next());
        track2Box.setSelectedItem(tracks.next());
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        this.setVisible(false);
    }

    private void okButtonActionPerformed(ActionEvent e) {

        FeatureTrack track1 = (FeatureTrack) track1Box.getSelectedItem();
        FeatureTrack track2 = (FeatureTrack) track2Box.getSelectedItem();
        CombinedFeatureSource source = new CombinedFeatureSource(new FeatureSource[]{track1.source, track2.source},
                (CombinedFeatureSource.Operation) operation.getSelectedItem());
        Track newTrack = new FeatureTrack(track1.getId() + track2.getId(), resultName.getText(), source);
        IGV.getInstance().getTrackPanel(IGV.FEATURE_PANEL_NAME).addTrack(newTrack);

        this.setVisible(false);

        IGV.getInstance().repaint();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        track1Box = new JComboBox();
        operation = new JComboBox();
        track2Box = new JComboBox();
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        resultName = new JTextArea();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setAlwaysOnTop(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(null);
                contentPanel.add(track1Box);
                track1Box.setBounds(50, 35, 190, track1Box.getPreferredSize().height);
                contentPanel.add(operation);
                operation.setBounds(50, 75, 190, operation.getPreferredSize().height);
                contentPanel.add(track2Box);
                track2Box.setBounds(50, 115, 190, track2Box.getPreferredSize().height);

                //---- label1 ----
                label1.setText("Result Track Name");
                contentPanel.add(label1);
                label1.setBounds(50, 150, 128, label1.getPreferredSize().height);

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(resultName);
                }
                contentPanel.add(scrollPane1);
                scrollPane1.setBounds(50, 180, 175, 35);

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for (int i = 0; i < contentPanel.getComponentCount(); i++) {
                        Rectangle bounds = contentPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = contentPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    contentPanel.setMinimumSize(preferredSize);
                    contentPanel.setPreferredSize(preferredSize);
                }
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 80, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                buttonBar.add(okButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 0, 0), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JComboBox track1Box;
    private JComboBox operation;
    private JComboBox track2Box;
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JTextArea resultName;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private class TrackComboBoxRenderer extends DefaultListCellRenderer {


        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Track track = (Track) value;
            String toShow = track.getName();
            return super.getListCellRendererComponent(list, toShow, index, isSelected, cellHasFocus);
        }
    }

    private void setOutputTrackName() {
        String name = ((Track) track1Box.getSelectedItem()).getName();
        name += " " + operation.getSelectedItem() + " ";
        name += ((Track) track2Box.getSelectedItem()).getName();
        resultName.setText(name);
    }

    private class SetOutputTrackNameListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            setOutputTrackName();
        }
    }
}
