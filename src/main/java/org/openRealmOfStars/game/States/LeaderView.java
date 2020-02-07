package org.openRealmOfStars.game.States;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openRealmOfStars.game.GameCommands;
import org.openRealmOfStars.gui.ListRenderers.LeaderListRenderer;
import org.openRealmOfStars.gui.buttons.SpaceButton;
import org.openRealmOfStars.gui.infopanel.InfoPanel;
import org.openRealmOfStars.gui.labels.InfoTextArea;
import org.openRealmOfStars.gui.mapPanel.MapPanel;
import org.openRealmOfStars.gui.panels.BlackPanel;
import org.openRealmOfStars.gui.utilies.GuiStatics;
import org.openRealmOfStars.player.PlayerInfo;
import org.openRealmOfStars.player.fleet.Fleet;
import org.openRealmOfStars.player.leader.Job;
import org.openRealmOfStars.player.leader.Leader;
import org.openRealmOfStars.player.leader.LeaderUtility;
import org.openRealmOfStars.player.leader.Perk;
import org.openRealmOfStars.starMap.StarMap;
import org.openRealmOfStars.starMap.planet.Planet;
import org.openRealmOfStars.starMap.planet.construction.Building;

/**
*
* Open Realm of Stars game project
* Copyright (C) 2020 Tuomo Untinen
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, see http://www.gnu.org/licenses/
*
*
* Research view for handling researching technology for player
*
*/
public class LeaderView extends BlackPanel  implements ListSelectionListener {

  /**
  *
  */
  private static final long serialVersionUID = 1L;
  /**
   * Player Info
   */
  private PlayerInfo player;
  /**
   * Star map
   */
  private StarMap map;

  /**
   * JList of leaders in order
   */
  private JList<Leader> leaderList;

  /**
   * Info Text for Leader
   */
  private InfoTextArea infoText;

  /**
   * Map Panel for drawing small starmap where leader is assigned.
   */
  private MapPanel mapPanel;

  /**
   * Leader Cost for recruit
   */
  private int leaderCost;

  /**
   * Planet where training happens
   */
  private Planet trainingPlanet;

  /**
   * Recruit button.
   */
  private SpaceButton recruitBtn;
  /**
   * View Leader view.
   * @param info Player info
   * @param starMap Star map data
   * @param listener ActionListener.
   */
  public LeaderView(final PlayerInfo info, final StarMap starMap,
      final ActionListener listener) {
    player = info;
    map = starMap;
    InfoPanel base = new InfoPanel();
    base.setTitle("Leaders");
    this.setLayout(new BorderLayout());
    base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));
    InfoPanel center = new InfoPanel();
    center.setTitle("Leader");
    center.setLayout(new BorderLayout());
    Leader[] leaders = sortLeaders(player.getLeaderPool());
    leaderList = new JList<>(leaders);
    leaderList.setCellRenderer(new LeaderListRenderer());
    leaderList.addListSelectionListener(this);
    JScrollPane scroll = new JScrollPane(leaderList);
    scroll.setBackground(GuiStatics.COLOR_DEEP_SPACE_PURPLE_DARK);
    leaderList.setBackground(Color.BLACK);
    leaderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    base.add(scroll);
    scroll.setAlignmentX(CENTER_ALIGNMENT);
    recruitBtn = new SpaceButton("Recruit leader",
        GameCommands.COMMAND_RECRUIT_LEADER);
    leaderCost = LeaderUtility.leaderRecruitCost(info);
    trainingPlanet = LeaderUtility.getBestLeaderTrainingPlanet(
        map.getPlanetList(), player);
    updateRecruitButtonToolTips();
    recruitBtn.addActionListener(listener);
    recruitBtn.setAlignmentX(CENTER_ALIGNMENT);
    base.add(recruitBtn);
    infoText = new InfoTextArea(20, 35);
    infoText.setEditable(false);
    infoText.setFont(GuiStatics.getFontCubellanSmaller());
    scroll = new JScrollPane(infoText);
    scroll.setBackground(GuiStatics.COLOR_DEEP_SPACE_PURPLE_DARK);
    center.add(scroll, BorderLayout.WEST);
    mapPanel = new MapPanel(false);
    center.add(mapPanel, BorderLayout.CENTER);
    // Bottom panel
    InfoPanel bottomPanel = new InfoPanel();
    bottomPanel.setLayout(new BorderLayout());
    bottomPanel.setTitle(null);
    SpaceButton btn = new SpaceButton("Back to star map",
        GameCommands.COMMAND_VIEW_STARMAP);
    btn.addActionListener(listener);
    bottomPanel.add(btn, BorderLayout.CENTER);
    // Add panels to base
    this.add(bottomPanel, BorderLayout.SOUTH);
    this.add(base, BorderLayout.WEST);
    this.add(center, BorderLayout.CENTER);
  }

  /**
   * Update Recruit button tool tips.
   */
  private void updateRecruitButtonToolTips() {
    if (trainingPlanet != null && leaderCost <= player.getTotalCredits()) {
      recruitBtn.setToolTipText("<html>Recruit new leader with " + leaderCost
          + " credits.<br> This will also use one population from planet "
          + trainingPlanet.getName() + "."
          + "</html>");
    } else if (trainingPlanet == null) {
      recruitBtn.setToolTipText("<html>Your realm does not have more than"
          + "<br> 4 population on any of your planets."
          + "</html>");
      recruitBtn.setEnabled(false);
    } else if (leaderCost > player.getTotalCredits()) {
      recruitBtn.setToolTipText("<html>Your realm does not have enough"
          + "credits to recruit leader.<br> Leader recruit costs "
          + leaderCost + " credits. Your realm has " + player.getTotalCredits()
          + " credits."
          + "</html>");
      recruitBtn.setEnabled(false);
    }
  }
  /**
   * Update all panels.
   */
  public void updatePanel() {
    Leader leader = leaderList.getSelectedValue();
    if (leader != null) {
      if (leader.getJob() == Job.RULER) {
        for (Planet planet : map.getPlanetList()) {
          if (planet.getPlanetPlayerInfo() == player) {
            map.setDrawPos(planet.getX(), planet.getY());
            break;
          }
        }
      } else if (leader.getJob() == Job.GOVERNOR) {
        for (Planet planet : map.getPlanetList()) {
          if (planet.getGovernor() == leader) {
            map.setDrawPos(planet.getX(), planet.getY());
            break;
          }
        }
      } else if (leader.getJob() == Job.COMMANDER) {
        for (int i = 0; i < player.getFleets().getNumberOfFleets(); i++) {
          Fleet fleet = player.getFleets().getByIndex(i);
          if (fleet.getCommander() == leader) {
            map.setDrawPos(fleet.getX(), fleet.getY());
            break;
          }
        }
      } else {
        map.setDrawPos(map.getMaxX() / 2, map.getMaxY() / 2);
      }
      mapPanel.drawMap(map);
      StringBuilder sb = new StringBuilder();
      sb.append(leader.getDescription());
      sb.append("\n\nPerks\n");
      if (leader.getPerkList().size() == 0) {
        sb.append("None");
      } else {
        for (Perk perk : leader.getPerkList()) {
          sb.append("* ");
          sb.append(perk.getName());
          sb.append(" ");
          sb.append(perk.getDescription());
          sb.append("\n");
        }
      }
      infoText.setText(sb.toString());
    }
    updateRecruitButtonToolTips();
    this.repaint();
  }
  /**
   * Sort all leaders in following order:
   * Ruler, Unassinged, Governor, Commander, Too young
   * @param leaders Leaders in arraylist
   * @return Array of leaders in order.
   */
  public static final Leader[] sortLeaders(final ArrayList<Leader> leaders) {
    Leader[] result = new Leader[leaders.size()];
    int index = 0;
    // Get leader
    for (Leader leader : leaders) {
      if (leader.getJob() == Job.RULER) {
        result[index] = leader;
        index++;
        break;
      }
    }
    // Get all unassigned
    for (Leader leader : leaders) {
      if (leader.getJob() == Job.UNASSIGNED) {
        result[index] = leader;
        index++;
      }
    }
    // Get all governors
    for (Leader leader : leaders) {
      if (leader.getJob() == Job.GOVERNOR) {
        result[index] = leader;
        index++;
      }
    }
    // Get all commanders
    for (Leader leader : leaders) {
      if (leader.getJob() == Job.COMMANDER) {
        result[index] = leader;
        index++;
      }
    }
    // Get all too youngs
    for (Leader leader : leaders) {
      if (leader.getJob() == Job.TOO_YOUNG) {
        result[index] = leader;
        index++;
      }
    }
    // Get all too deads
    for (Leader leader : leaders) {
      if (leader.getJob() == Job.DEAD) {
        result[index] = leader;
        index++;
      }
    }
    return result;
  }

  @Override
  public void valueChanged(final ListSelectionEvent arg0) {
    updatePanel();
  }

  /**
   * Handle actions in leader view.
   * @param arg0 Action event to handle
   */
  public void handleActions(final ActionEvent arg0) {
    if (arg0.getActionCommand().equals(GameCommands.COMMAND_RECRUIT_LEADER)
        && player.getTotalCredits() >= leaderCost) {
      player.setTotalCredits(player.getTotalCredits() - leaderCost);
      int level = 1;
      int xp = 0;
      for (Building building : trainingPlanet.getBuildingList()) {
        if (building.getName().equals("Barracks")) {
          xp = 50;
        }
        if (building.getName().equals("Space academy")) {
          level++;
        }
      }
      Leader leader = LeaderUtility.createLeader(player, trainingPlanet, level);
      leader.setExperience(xp);
      leader.setJob(Job.UNASSIGNED);
      player.getLeaderPool().add(leader);
      trainingPlanet.takeColonist();
      Leader[] leaders = sortLeaders(player.getLeaderPool());
      leaderList.setListData(leaders);
      leaderCost = LeaderUtility.leaderRecruitCost(player);
      trainingPlanet = LeaderUtility.getBestLeaderTrainingPlanet(
          map.getPlanetList(), player);
      updatePanel();
    }
  }
}
