/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chess.util;

/**
 *
 * @author Van
 */
public enum DataField {
    FT_VERSION(0, "FT_VERSION"),
    FT_TEMPGROWTH(1, "FT_TEMPGROWTH"),
    FT_ATTRIBUTE(2, "FT_ATTRIBUTE"),
    FT_GROWTH(3, "FT_GROWTH"),
    FT_TEMPPHYSICALGROWTH(4, "FT_TEMPPHYSICALGROWTH"),
    FT_PHYSICALGROWTH(5, "FT_PHYSICALGROWTH"),
    FT_ARCHETYPE(6, "FT_ARCHETYPE"),
    FT_ISMALE(7, "FT_ISMALE"),
    FT_HAIRINDEX(8, "FT_HAIRINDEX"),
    FT_FACIALHAIRINDEX(9, "FT_FACIALHAIRINDEX"),
    FT_HAIRCOLORINDEX(10, "FT_HAIRCOLORINDEX"),
    FT_HATINDEX(11, "FT_HATINDEX"),
    FT_HATCOLORINDEX(12, "FT_HATCOLORINDEX"),
    FT_GLASSESINDEX(13, "FT_GLASSESINDEX"),
    FT_HEADDECALINDEX(14, "FT_HEADDECALINDEX"),
    FT_HATDECALINDEX(15, "FT_HATDECALINDEX"),
    FT_TORSOINDEX(16, "FT_TORSOINDEX"),
    FT_TORSOCOLORINDEX(17, "FT_TORSOCOLORINDEX"),
    FT_PANTSINDEX(18,"FT_PANTSINDEX"),
    FT_PANTSCOLORINDEX(19,"FT_PANTSCOLORINDEX"),
    FT_TORSODECALINDEX(20,"FT_TORSODECALINDEX"),
    FT_TORSODECALCOLORINDEX(21,"FT_TORSODECALCOLORINDEX"),
    FT_BACKINDEX(22,"FT_BACKINDEX"),
    FT_BELTINDEX(23,"FT_BELTINDEX"),
    FT_NECKINDEX(24,"FT_NECKINDEX"),
    FT_JACKETINDEX(25,"FT_JACKETINDEX"),
    FT_JACKETCOLORINDEX(26,"FT_JACKETCOLORINDEX"),
    FT_FEETINDEX(27,"FT_FEETINDEX"),
    FT_FEETCOLORINDEX(28,"FT_FEETCOLORINDEX"),
    FT_SKINCOLOR(29,"FT_SKINCOLOR"),
    FT_BACKGROUNDTYPE(30,"FT_SKINCOLOR"),
    FT_CUSTOMDATA(31,"FT_CUSTOMDATA"),
    FT_MUSICLIST(32,"FT_MUSICLIST"),
    FT_BOOKLIST(33,"FT_BOOKLIST"),
    FT_GAMELIST(34,"FT_GAMELIST"),
    FT_PETLIST	(35,"FT_PETLIST"),
    FT_CATLIST	(36,"FT_CATLIST"),
    FT_TVLIST	(37,"FT_CATLIST"),
    FT_JOBLIST	(38,"FT_JOBLIST"),
    FT_LOC1	(39,"FT_LOC1"),
    FT_JOURNALGREETING(40,"FT_JOURNALGREETING"),
    FT_JOURNALSTATUS(41,"FT_JOURNALSTATUS"),
    FT_TOPFRIENDSIZE(42,"FT_TOPFRIENDSIZE"),
    FT_TOPFRIEND(43,"FT_TOPFRIEND"),
    FT_RIVALSIZE(44,"FT_RIVALSIZE"),
    FT_RIVAL	(45,"FT_RIVAL"),
    FT_LOVER	(46,"FT_LOVER"),
    FT_TEMPSTAT(47,"FT_TEMPSTAT"),
    FT_STAT	(48,"FT_STAT"),
    FT_LOCKEDARCH(49,"FT_LOCKEDARCH"),
    FT_LOCKEDFOOD(50,"FT_LOCKEDFOOD"),
    FT_LOCKEDTOY(51,"FT_LOCKEDTOY"),
    FT_LOCKEDCOMP(52,"FT_LOCKEDCOMP"),
    FT_USERNAME(53,"FT_USERNAME"),
    FT_PASSWORD(54,"FT_PASSWORD"),
    FT_ZIP	(55,"FT_ZIP"),
    FT_BUDDYSIZE(56,"FT_BUDDYSIZE"),
    FT_BUDDYNAME(57,"FT_BUDDYNAME"),
    FT_BUDDYSTATUS(58,"FT_BUDDYSTATUS"),
    FT_BUDDYSCORE(59,"FT_BUDDYSCORE"),
    FT_BUDDYBSTATUS(60,"FT_BUDDYBSTATUS"),
    FT_AGE	(61,"FT_AGE"),
    FT_RELATIONSHIPSTATUS(62,"FT_RELATIONSHIPSTATUS"),
    FT_ETHNICITY(63,"FT_ETHNICITY"),
    FT_GENDER	(64,"FT_GENDER"),
    FT_ORIENTATION(65,"FT_ORIENTATION"),
    FT_GROUPAFFILIATION(66,"FT_GROUPAFFILIATION"),
    FT_SEARCHRADIUS(67,"FT_SEARCHRADIUS"),
    FT_SEARCHAGEMIN(68,"FT_SEARCHAGEMIN"),
    FT_SEARCHAGEMAX(69,"FT_SEARCHAGEMAX"),
    FT_SEARCHRELATIONSHIP(70,"FT_SEARCHRELATIONSHIP"),
    FT_SEARCHETHNICITY(71,"FT_SEARCHETHNICITY"),
    FT_SEARCHGENDER(72,"FT_SEARCHGENDER"),
    FT_SEARCHORIENTATION(73,"FT_SEARCHORIENTATION"),
    FT_SEARCHGROUP(74,"FT_SEARCHGROUP"),
    FT_NETWORKSETTING(75,"FT_NETWORKSETTING"),
    FT_HASACCOUNT(76,"FT_HASACCOUNT"),
    FT_NEWEGO	(77,"FT_NEWEGO"),
    FT_ISFILTERON(78,"FT_ISFILTERON"),
    FT_TUTORIALSTEP(79,"FT_TUTORIALSTEP"),
    FT_IMAGEINDEX(80,"FT_IMAGEINDEX"),
    FT_ISPREDICTIVE(81,"FT_ISPREDICTIVE"),
    FT_ISCHATROOMPUBLIC(82,"FT_ISCHATROOMPUBLIC"),
    FT_FIRSTATTCHANGE(83,"FT_FIRSTATTCHANGE"),
    FT_RETIRED	(84,"FT_RETIRED"),
    FT_JUDGEMENT(85,"FT_JUDGEMENT"),
    FT_FILEID	(86,"FT_FILEID"),
    FT_NAME	(87,"FT_NAME"),
    FT_LOC2	(88,"FT_LOC2"),
    FT_UNIQUEDEVICEID(89,"FT_UNIQUEDEVICEID"),
    FT_GIFT        (90,"FT_GIFT"),
    FT_LEVEL        (91,"FT_LEVEL"),
    FT_PLACEHOLDER(92,"FT_PLACEHOLDER");
    
    private int mPos = -1;
    private String mTitle = "";
    
    DataField(int aPos, String aTitle)
    {
        mPos = aPos;
        mTitle = aTitle;
    }

    public int getPos() {
        return mPos;
    }

    public String getTitle() {
        return mTitle;
    }

    public static String getTitle(int aPos) 
    {
        for (DataField df : DataField.values())
        {
            if( df.mPos == aPos)
            {
                return df.mTitle;
            }
        }

        return null;
    }

}