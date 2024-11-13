/**
 * SENG 300 Project - Group 1:
 *
 * Avery Keuben - 30170731
 * Moiz Siddiqui - 30150291
 * Ammaar Melethil - 30141956
 * Joey Fisher - 30105628
 * Ethan Pangilinan - 30179143
 * Joshua Kraft - 30171525
 * Nathan Vaters - 30121908
 * Max Butcher - 30149202
 * Neeraj Ghansela - 30157473
 * Ansel Sulejmani - 30178521
 * Suleman Basit - 30132816
 * Jacob Boyden - 30193220
 * Cheshta Sharma - 30064538
 * Callum Bates - 30188601
 * Armughan Mustafa - 30154601
 * Connor Ell - 30073291
 * Saif Farag - 30195046
 * Ivan Agalakov - 30172107
 * Samuel Turner - 10064857
 * Stephanie Sevilla - 30176781
 * Winston Wang - 30185321
 */
package com.thelocalmarketplace.software.membership;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents the membership database in lieu of real database software
 */
public class MembershipDatabase {

    private static MembershipDatabase instance;

    private Map<Long, Membership> memberIDDatabase;//Long represents user id
    private Map<String, Membership> memberNameDatabase;
    public static MembershipDatabase getInstance() {
        return instance;
    }

    private MembershipDatabase() {
    	memberIDDatabase = new HashMap<>();
    	memberNameDatabase = new HashMap<>();
    }

    public static void initialize() throws RuntimeException {
        if(instance != null){
            throw new RuntimeException("Database already exists");
        }

        instance = new MembershipDatabase();
    }
    
    public static void uninitialize() throws RuntimeException {
        instance = null;
    }

    /**
     * Check if database has member with id
     * @param id id number to check
     * @return bool conditional on member database containing member with id
     */
    public boolean validateMembership(long id){
        return memberIDDatabase.containsKey(id);
    }
    public boolean validateMembership(String name){
        return memberNameDatabase.containsKey(name);
    }

    /**
     * Get the points a member has
     * @param id member id
     * @return Points the member has. Always 0 if the member isn't in the database
     */
    public long getMemberPoints(long id){
        return validateMembership(id) ? memberIDDatabase.get(id).getMemberPoints() : 0;
    }

    /**
     * Adjust the amount of points a membership has
     * @param id The id of the member whose points are to be changed
     * @param points The points value (pos/neg) to adjust by
     */
    public void adjustMemberPoints(long id, long points){
        //In case membership doesn't get validated first
        if(validateMembership(id)){
            memberIDDatabase.get(id).changeMemberPoints(points);
        }

    }

    /**
     * Add a new membership to the database
     * @param memberName The name of the member
     * @return The id assigned to the new member. Will return -1 if member with same name
     */
    public long addNewMembership(String memberName){
        if(memberNameDatabase.containsKey(memberName)){
            return -1;
        }
        long newid = memberIDDatabase.size();
        Membership newMember = new Membership(memberName, newid, 0);
        memberIDDatabase.put(newid, newMember);
        memberNameDatabase.put(memberName, newMember);
        return newid;
    }
    
    /**
     * Gets a member name from the member ID. Returns null
     * if there is no such member.
     * @param id
     * @return
     */
    public String getMemberName(long id) {
    	if(!validateMembership(id)) return null;
    	
    	return memberIDDatabase.get(id).getMemberName();
    }
}
