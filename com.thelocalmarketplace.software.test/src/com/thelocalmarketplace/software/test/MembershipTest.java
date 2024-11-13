package com.thelocalmarketplace.software.test;

import com.thelocalmarketplace.software.membership.Membership;
import com.thelocalmarketplace.software.membership.MembershipDatabase;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.TreeMap;


public class MembershipTest {

    Membership my_Membership;
  
    @Before
    public void setup() {
        my_Membership = new Membership("joe", 1, 100);
    }
    
    @Test
    public void TestConstructor() {
        assertEquals(my_Membership.getMemberName(), "joe");
        assertEquals(my_Membership.getMembershipNumber(), 1);
        assertEquals(my_Membership.getMemberPoints(), 100);
    }

    @Test
    public void TestgetMemberName() {
        assertEquals(my_Membership.getMemberName(), "joe");
    }

    @Test
    public void TestgetMembershipNumber() {
        assertEquals(my_Membership.getMembershipNumber(), 1);
    }

    @Test
    public void TestgetMemberPoints() {
        assertEquals(my_Membership.getMemberPoints(), 100);
    }

    @Test
    public void TestchangeMemberPoints() {
        my_Membership.changeMemberPoints(1000);
        assertEquals(my_Membership.getMemberPoints(), 1100);
    }
    
    @Test
    public void TestMembershipDatabse() {
    	MembershipDatabase.uninitialize();
    	MembershipDatabase.initialize();
    	MembershipDatabase database = MembershipDatabase.getInstance();
    	long id = database.addNewMembership("Test");
    	database.adjustMemberPoints(id, 200);
    	assertEquals(200, database.getMemberPoints(id));
    }

}
