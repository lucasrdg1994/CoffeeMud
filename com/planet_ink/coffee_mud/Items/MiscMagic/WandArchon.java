package com.planet_ink.coffee_mud.Items.MiscMagic;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;

public class WandArchon extends StdWand
{
	public WandArchon()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		name="a flashy wand";
		displayText="a flashy wand has been left here.";
		description="A wand made out of sparkling energy.";
		secretIdentity="The Wand of the Archons!";
		this.setUsesRemaining(99999);
		baseGoldValue=20000;
		recoverEnvStats();
	}

	public Environmental newInstance()
	{
		return new WandArchon();
	}

	public void waveIfAble(MOB mob,
						   Environmental afftarget,
						   String message,
						   Wand me)
	{
		if((mob.isMine(me))
		   &&(!me.amWearingAt(Item.INVENTORY)))
		{
			if((mob.location()!=null)&&(afftarget!=null)&&(afftarget instanceof MOB))
			{
				MOB target=(MOB)afftarget;
				if(message.toUpperCase().indexOf("LEVEL ALL UP")>0)
				{
					mob.location().show(mob,target,Affect.MSG_OK_VISUAL,me.name()+" glows brightly at <T-NAME>.");
					while(target.envStats().level()<30)
						target.charStats().getMyClass().gainExperience(target,null,target.getExpNeededLevel()+1);
				}
				else
				if(message.toUpperCase().indexOf("LEVEL UP")>0)
				{
					mob.location().show(mob,target,Affect.MSG_OK_VISUAL,me.name()+" glows brightly at <T-NAME>.");
					target.charStats().getMyClass().gainExperience(target,null,target.getExpNeededLevel()+1);
					return;
				}
				else
				if(message.toUpperCase().indexOf("REFRESH")>0)
				{
					mob.location().show(mob,target,Affect.MSG_OK_VISUAL,me.name()+" glows brightly at <T-NAME>.");
					target.resetToMaxState();
					target.recoverMaxState();
					target.tell("You feel refreshed!");
					return;
				}
				else
				if(message.toUpperCase().indexOf("BURN")>0)
				{
					mob.location().show(mob,target,Affect.MSG_OK_VISUAL,me.name()+" wielded by <S-NAME> shoots forth magical green flames at <T-NAME>.");
					int flameDamage = (int) Math.round( Math.random() * 6 );
					flameDamage *= 3;
					mob.location().show(mob,target,Affect.MSG_OK_ACTION,me.name()+" "+ExternalPlay.standardHitWord(Weapon.TYPE_BURNING,flameDamage)+" <T-NAME>!");
					ExternalPlay.postDamage(mob,target,null,(++flameDamage));
					return;
				}
			}
		}
		super.waveIfAble(mob,afftarget,message,me);
	}
}
