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

/**
 * Class to represent a customers membership
 * Uses a string for the customers name
 * Uses a long int as an ID
 */
public class Membership {
    private String memberName;
    private long membershipNumber;
    private long memberPoints;

    public Membership(String memberName, long membershipNumber, long memberPoints) {
        this.memberName = memberName;
        this.membershipNumber = membershipNumber;
        this.memberPoints = memberPoints;
    }

    public String getMemberName() {
        return memberName;
    }

    public long getMembershipNumber() {
        return membershipNumber;
    }

    public long getMemberPoints() {
        return memberPoints;
    }

    public void changeMemberPoints(long memberPoints) {
        this.memberPoints += memberPoints;
    }
}
