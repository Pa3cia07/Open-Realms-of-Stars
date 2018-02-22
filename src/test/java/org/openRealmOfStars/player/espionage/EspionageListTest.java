package org.openRealmOfStars.player.espionage;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openRealmOfStars.player.fleet.FleetType;

/**
*
* Open Realm of Stars game project
* Copyright (C) 2018  Tuomo Untinen
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
* Espionage List Test
*
*/
public class EspionageListTest {

  @Test
  @Category(org.openRealmOfStars.UnitTest.class)
  public void testBasic() {
    EspionageList list = new EspionageList(3);
    assertEquals(0, list.getSize());
    assertEquals(null, list.getEspionage(0));
    assertEquals(3, list.getPlayerIndex());
    assertEquals(0, list.getTotalBonus());
    list.addEspionageBonus(EspionageBonusType.SPY_FLEET, 1, "Fleet #1");
    assertEquals(1, list.getSize());
    assertEquals(1, list.getTotalBonus());
    assertEquals("Fleet #1", list.getEspionage(0).getDescription());
    list.addEspionageBonus(EspionageBonusType.TRADE, 5, "Spy trade");
    assertEquals(2, list.getSize());
    assertEquals(6, list.getTotalBonus());
    assertEquals("Spy trade", list.getEspionage(1).getDescription());
    list.addEspionageBonus(EspionageBonusType.SPY_FLEET, 5, "Fleet #2");
    assertEquals(3, list.getSize());
    assertEquals(10, list.getTotalBonus());
    assertEquals("Fleet #2", list.getEspionage(2).getDescription());
  }

  @Test
  @Category(org.openRealmOfStars.UnitTest.class)
  public void testIsRecognized() {
    EspionageList list = new EspionageList(3);
    assertEquals(0, list.getSize());
    assertEquals(null, list.getEspionage(0));
    assertEquals(3, list.getPlayerIndex());
    assertEquals(0, list.getTotalBonus());
    list.addEspionageBonus(EspionageBonusType.SPY_FLEET, 1, "Fleet #1");
    assertEquals(1, list.getSize());
    assertEquals(1, list.getTotalBonus());
    assertEquals("Fleet #1", list.getEspionage(0).getDescription());
    list.addEspionageBonus(EspionageBonusType.TRADE, 2, "Spy trade");
    assertEquals(false, list.isFleetTypeRecognized(FleetType.NON_MILITARY));
    assertEquals(false, list.isFleetTypeRecognized(FleetType.STARBASE));
    assertEquals(false, list.isFleetTypeRecognized(FleetType.MILITARY));
    assertEquals(false, list.isFleetTypeRecognized(FleetType.PRIVATEER));
    assertEquals(2, list.getSize());
    assertEquals(3, list.getTotalBonus());
    assertEquals("Spy trade", list.getEspionage(1).getDescription());
    list.addEspionageBonus(EspionageBonusType.SPY_FLEET, 1, "Fleet #2");
    assertEquals(3, list.getSize());
    assertEquals(4, list.getTotalBonus());
    assertEquals("Fleet #2", list.getEspionage(2).getDescription());
    assertEquals(true, list.isFleetTypeRecognized(FleetType.NON_MILITARY));
    assertEquals(false, list.isFleetTypeRecognized(FleetType.STARBASE));
    assertEquals(false, list.isFleetTypeRecognized(FleetType.MILITARY));
    assertEquals(false, list.isFleetTypeRecognized(FleetType.PRIVATEER));
    list.addEspionageBonus(EspionageBonusType.SPY_FLEET, 2, "Fleet #3");
    assertEquals(true, list.isFleetTypeRecognized(FleetType.NON_MILITARY));
    assertEquals(true, list.isFleetTypeRecognized(FleetType.STARBASE));
    assertEquals(false, list.isFleetTypeRecognized(FleetType.MILITARY));
    assertEquals(false, list.isFleetTypeRecognized(FleetType.PRIVATEER));
    list.addEspionageBonus(EspionageBonusType.SPY_FLEET, 2, "Fleet #4");
    assertEquals(true, list.isFleetTypeRecognized(FleetType.NON_MILITARY));
    assertEquals(true, list.isFleetTypeRecognized(FleetType.STARBASE));
    assertEquals(true, list.isFleetTypeRecognized(FleetType.MILITARY));
    assertEquals(false, list.isFleetTypeRecognized(FleetType.PRIVATEER));
    list.addEspionageBonus(EspionageBonusType.SPY_FLEET, 2, "Fleet #5");
    assertEquals(true, list.isFleetTypeRecognized(FleetType.NON_MILITARY));
    assertEquals(true, list.isFleetTypeRecognized(FleetType.STARBASE));
    assertEquals(true, list.isFleetTypeRecognized(FleetType.MILITARY));
    assertEquals(true, list.isFleetTypeRecognized(FleetType.PRIVATEER));
  }

}