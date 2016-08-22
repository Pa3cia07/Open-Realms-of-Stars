package org.openRealmOfStars.game.States;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;

import org.openRealmOfStars.AI.PathFinding.AStarSearch;
import org.openRealmOfStars.AI.PathFinding.PathPoint;
import org.openRealmOfStars.game.GameCommands;
import org.openRealmOfStars.gui.BlackPanel;
import org.openRealmOfStars.gui.MapPanel;
import org.openRealmOfStars.gui.buttons.SpaceButton;
import org.openRealmOfStars.gui.infopanel.BattleInfoPanel;
import org.openRealmOfStars.gui.infopanel.InfoPanel;
import org.openRealmOfStars.player.PlayerInfo;
import org.openRealmOfStars.player.combat.Combat;
import org.openRealmOfStars.player.combat.CombatMapMouseListener;
import org.openRealmOfStars.player.combat.CombatShip;
import org.openRealmOfStars.player.fleet.Fleet;
import org.openRealmOfStars.player.ship.ShipImage;
import org.openRealmOfStars.starMap.StarMap;

/**
 * 
 * Open Realm of Stars game project
 * Copyright (C) 2016  Tuomo Untinen
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
 * Battle view for handling space combat
 * 
 */
public class BattleView extends BlackPanel {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * MapPanel for drawing the star map
   */
  private MapPanel mapPanel;

  /**
   * Current combat
   */
  private Combat combat;
  
  /**
   * Star map where combat happens
   */
  private StarMap map;

  /**
   * Infopanel on east side
   */
  private BattleInfoPanel infoPanel;
  
  /**
   * Combat map mouse listener
   */
  private CombatMapMouseListener combatMapMouseListener;
  
  /**
   * aStar search for AI
   */
  private AStarSearch aStar;

  /**
   * Delay count for AI, since it's too fast for humans
   */
  private int delayCount;
  
  /**
   * 2 Seconds with 75ms animation timer
   */
  private static final int MAX_DELAY_COUNT = 30;
  
  /**
   * Battle view for space combat
   * @param fleet1 First fleet in combat
   * @param player1 First player in combat
   * @param fleet2 Second fleet in combat
   * @param player2 Second player in combat
   * @param StarMap star map
   * @param listener ActionListener
   */
  public BattleView(Fleet fleet1, PlayerInfo player1, Fleet fleet2,
      PlayerInfo player2,StarMap map, ActionListener listener) {
    BlackPanel base = new BlackPanel();
    this.map = map;
    combat = new Combat(fleet1, fleet2, player1, player2);
    mapPanel = new MapPanel(true);
    mapPanel.setSize(Combat.MAX_X*ShipImage.MAX_WIDTH, 
        Combat.MAX_Y*ShipImage.MAX_HEIGHT);
    mapPanel.drawBattleMap(combat, player1, this.map);

    infoPanel = new BattleInfoPanel(combat.getCurrentShip(),listener);

    combatMapMouseListener = new CombatMapMouseListener(combat, mapPanel, infoPanel);
    mapPanel.addMouseListener(combatMapMouseListener);
    mapPanel.addMouseMotionListener(combatMapMouseListener);
    
    InfoPanel bottom = new InfoPanel();
    bottom.setTitle(null);
    bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
    SpaceButton btn = new SpaceButton("End round", GameCommands.COMMAND_END_BATTLE_ROUND);
    btn.addActionListener(listener);
    bottom.add(btn);
    

    
    this.setLayout(new BorderLayout());
    base.add(mapPanel,BorderLayout.CENTER);
    this.add(base,BorderLayout.CENTER);
    this.add(infoPanel,BorderLayout.EAST);
    this.add(bottom,BorderLayout.SOUTH);
    aStar = null;
    delayCount = 0;

  }

  
  /**
   * Update panels on BattleView
   */
  public void updatePanels() {
  }

  /**
   * Handle AI
   */
  private void handleAI() {
    PlayerInfo info = combat.getCurrentShip().getPlayer();
    CombatShip deadliest = combat.getMostPowerfulShip(info);
    CombatShip closest = combat.getClosestEnemyShip(info, combat.getCurrentShip());
    if (aStar == null) {
      aStar = new AStarSearch(combat, combat.getCurrentShip(),deadliest, 1);
      if (aStar.doSearch()) {
        aStar.doRoute();
      } else {
        // Could not found route for deadliest 
      }
    }
    PathPoint point = aStar.getNextMove();
    if (point != null && !combat.isBlocked(point.getX(), point.getY())) {
      combat.getCurrentShip().setMovesLeft(combat.getCurrentShip().getMovesLeft()-1);
      combat.getCurrentShip().setX(point.getX());
      combat.getCurrentShip().setY(point.getY());
    }
    if (combat.getCurrentShip().getMovesLeft() == 0 || aStar.isLastMove()) {
      aStar = null;
      endRound();
    }
  }
  
  /**
   * End battle round
   */
  private void endRound() {
    combat.nextShip();
    infoPanel.showShip(combat.getCurrentShip());
    this.repaint();
  }
  
  /**
   * Handle actions for battle view
   * @param arg0 Active Event
   */
  public void handleActions(ActionEvent arg0) {
    if (arg0.getActionCommand().equalsIgnoreCase(
        GameCommands.COMMAND_ANIMATION_TIMER) ) {
      delayCount++;
      if (delayCount >= MAX_DELAY_COUNT) {
        delayCount = 0;
      }
      if (!combat.getCurrentShip().getPlayer().isHuman() && delayCount == 0) {
        handleAI();
      }
      mapPanel.drawBattleMap(combat, map.getCurrentPlayerInfo(), map);
      mapPanel.repaint();
    }
    if (arg0.getActionCommand().equalsIgnoreCase(
        GameCommands.COMMAND_END_BATTLE_ROUND) && combat.getCurrentShip().getPlayer().isHuman()) {
      endRound();
    }

  }

}
