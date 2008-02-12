/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package net.java.visualvm.modules.glassfish.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import javax.swing.SwingUtilities;
import org.netbeans.lib.profiler.ui.charts.DynamicSynchronousXYChartModel;

/**
 *
 * @author  Jaroslav Bachorik
 */
public class FileCachePanel extends javax.swing.JPanel implements Observer {
    private final static class SimulationModel extends Model {
        Random rnd = new Random();
        Runnable updater = new Runnable(){

            @Override
            public void run() {
                while(true) {
                    setChanged();
                    notifyObservers();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {}
                }
            }
        };

        public SimulationModel() {
            new Thread(updater).start();
        }

        @Override
        public RangedLong getUtilizationAll() {
            return new RangedLong(0, 100, rnd.nextInt(100));
        }

        @Override
        public RangedLong getUtilizationHeap() {
            return new RangedLong(0, 200, rnd.nextInt(200));
        }
        
        @Override
        public RangedLong getUtilizationOpen() {
            return new RangedLong(0, 100, rnd.nextInt(100));
        }

        @Override
        public RangedLong getHitRatio() {
            return new RangedLong(0, 100, rnd.nextInt(100));
        }
    }
    
    private Model fileCacheStatsModel;
    private Chart chart;
    
    /** Creates new form ConnectionQueuePanel */
    public FileCachePanel() {
//        fileCacheStatsModel = new SimulationModel();
//        setModel(fileCacheStatsModel);
        initComponents();
        initComponents2();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        labelHitRation = new javax.swing.JLabel();
        hitRatio = new com.sun.tools.visualvm.core.ui.components.LevelIndicator();
        chartLegend = new javax.swing.JPanel();
        chartPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setBackground(java.awt.Color.white);

        labelHitRation.setText(org.openide.util.NbBundle.getMessage(FileCachePanel.class, "FileCachePanel.labelHitRatio.text")); // NOI18N

        hitRatio.setAutoRepaint(false);
        hitRatio.setFollowPeak(true);

        javax.swing.GroupLayout hitRatioLayout = new javax.swing.GroupLayout(hitRatio);
        hitRatio.setLayout(hitRatioLayout);
        hitRatioLayout.setHorizontalGroup(
            hitRatioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 260, Short.MAX_VALUE)
        );
        hitRatioLayout.setVerticalGroup(
            hitRatioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 11, Short.MAX_VALUE)
        );

        chartLegend.setOpaque(false);
        chartLegend.setLayout(new java.awt.BorderLayout());

        chartPanel.setBorder(null);
        chartPanel.setOpaque(false);
        chartPanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(FileCachePanel.class, "FileCachePanel.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chartPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                    .addComponent(chartLegend, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(labelHitRation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hitRatio, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelHitRation)
                    .addComponent(hitRatio, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chartLegend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartLegend;
    private javax.swing.JPanel chartPanel;
    private com.sun.tools.visualvm.core.ui.components.LevelIndicator hitRatio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelHitRation;
    // End of variables declaration//GEN-END:variables

    public Model getModel() {
        return fileCacheStatsModel;
    }

    public void setModel(Model connQueueStatsModel) {
        if (this.fileCacheStatsModel != null) {
            this.fileCacheStatsModel.deleteObserver(this);
        }
        this.fileCacheStatsModel = connQueueStatsModel;
        connQueueStatsModel.addObserver(this);
    }
    
    public static abstract class Model extends GenericModel {
        public abstract RangedLong getUtilizationHeap();
        public abstract RangedLong getUtilizationAll();
        public abstract RangedLong getUtilizationOpen();
        public abstract RangedLong getHitRatio();
    }

    public Model getConnQueueStatsModel() {
        return fileCacheStatsModel;
    }

    public void setConnQueueStatsModel(Model connQueueStatsModel) {
        if (this.fileCacheStatsModel != null) {
            this.fileCacheStatsModel.deleteObserver(this);
        }
        this.fileCacheStatsModel = connQueueStatsModel;
    }

    public void update(Observable o, Object arg) {
        hitRatio.setMinimum(fileCacheStatsModel.getHitRatio().min);
        hitRatio.setMaximum(fileCacheStatsModel.getHitRatio().max);
        hitRatio.setValue(fileCacheStatsModel.getHitRatio().val);
        
        if (chart != null && chart.getChartModel() != null) {
            long values[] = new long[] { getPercent(fileCacheStatsModel.getUtilizationAll()), getPercent(fileCacheStatsModel.getUtilizationOpen()), getPercent(fileCacheStatsModel.getUtilizationHeap())};
            chart.getChartModel().addItemValues(System.currentTimeMillis(), values);
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                repaint();
            }
        });
    }
    
    private void initComponents2() {
        chart = new Chart() {
            @Override
            protected void setupModel(DynamicSynchronousXYChartModel xyChartModel) {
                xyChartModel.setupModel(new String[]{"Overall", "Open", "Heap"}, new Color[]{Color.BLUE, Color.GREEN, Color.YELLOW});
            }
        };
        
        chartPanel.add(chart, BorderLayout.CENTER);
        chartLegend.add(chart.createBigLegend(), BorderLayout.EAST);
    }
    
    private int getPercent(GenericModel.RangedLong value) {
        return Math.round(((float)value.val / (float)(value.max - value.min)) * 100f);
    }
}
