package it.polimi.db2.mission.services;

import javax.ejb.EJB;

import it.polimi.db2.mission.entities.Mission;
import javax.ejb.Stateless;

@Stateless
public class HorizontalPropagationTesterService {
	@EJB(name = "it.polimi.db2.mission.services/ProjectService")
	private ProjectService prjService;
	@EJB(name = "it.polimi.db2.mission.services/MissionService")
	private MissionService mService;

	public void testPropagation() {
		System.out.println("Entering method testPropagation");
		Mission m = null;
		Integer missionId = null;
		try {
			m = mService.findUnassignedMission();
			missionId = m.getId();
			System.out.println("Mission id in method testPropagation is " + m.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			prjService.associateMission(1, missionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
