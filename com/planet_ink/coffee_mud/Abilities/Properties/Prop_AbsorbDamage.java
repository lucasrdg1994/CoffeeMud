package com.planet_ink.coffee_mud.Abilities.Properties;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Prop_AbsorbDamage extends Property
{
	public String ID() { return "Prop_AbsorbDamage"; }
	public String name(){ return "Absorb Damage";}
	protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS;}
	public Environmental newInstance(){	Prop_AbsorbDamage BOB=new Prop_AbsorbDamage();	BOB.setMiscText(text());return BOB;}

	public String accountForYourself()
	{
		String id="Absorbs damage of the following amount and types: "+text();
		return id;
	}

	public boolean okMessage(Environmental myHost, CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if((affected!=null)
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)&&(msg.value()>0))
		{
			MOB M=null;
			if(affected instanceof MOB)
				M=(MOB)affected;
			else
			if((affected instanceof Item)
			&&(!((Item)affected).amWearingAt(Item.INVENTORY))
			&&(((Item)affected).owner()!=null)
			&&(((Item)affected).owner() instanceof MOB))
				M=(MOB)((Item)affected).owner();
			if(M==null) return true;
			if(!msg.amITarget(M)) return true;
			
			String text=text().toUpperCase();
			
			int immune=text.indexOf("+ALL");
			int x=-1;
			for(int i=0;i<CharStats.affectTypeMap.length;i++)
				if((CharStats.affectTypeMap[i]==msg.sourceMinor())
				&&((msg.tool()==null)||(i!=CharStats.SAVE_MAGIC)))
				{
					Vector V=Util.parse(CharStats.TRAITS[i]);
					if(((String)V.lastElement()).equals("SAVE"))
						x=text.indexOf((String)V.firstElement());
					else
						x=text.indexOf((String)V.lastElement());
					if(x>0)
					{
						if((text.charAt(x-1)=='-')&&(immune>=0))
							immune=-1;
						else
						if(text.charAt(x-1)!='-')
							immune=x;
					}
				}
			
			if((x<0)&&(msg.tool() instanceof Weapon))
			{
				x=text.indexOf(Weapon.typeDescription[((Weapon)msg.tool()).weaponType()]);
				if(x<0) x=(Sense.isABonusItems(msg.tool()))?text.indexOf("MAGIC"):-1;
				if(x<0) x=text.indexOf(EnvResource.RESOURCE_DESCS[((Weapon)msg.tool()).material()&EnvResource.RESOURCE_MASK]);
				if(x>0)
				{
					if((text.charAt(x-1)=='-')&&(immune>=0))
						immune=-1;
					else
					if(text.charAt(x-1)!='-')
						immune=x;
				}
				else
				{
					x=text.indexOf("LEVEL");
					if(x>0)
					{
						String lvl=text.substring(x+5);
						if(lvl.indexOf(" ")>=0)
							lvl=lvl.substring(lvl.indexOf(" "));
						if((text.charAt(x-1)=='-')&&(immune>=0))
						{
							if(msg.tool().envStats().level()>=Util.s_int(lvl))
								immune=-1;
						}
						else
						if(text.charAt(x-1)!='-')
						{
							if(msg.tool().envStats().level()<Util.s_int(lvl))
								immune=x;
						}
					}
				}
			}
			
			if((x<0)&&(msg.tool() instanceof Ability))
			{
				int classType=((Ability)msg.tool()).classificationCode()&Ability.ALL_CODES;
				switch(classType)
				{
				case Ability.SPELL:
				case Ability.PRAYER:
				case Ability.CHANT:
				case Ability.SONG:
					{
						x=text.indexOf("MAGIC");
						if(x>0)
						{
							if((text.charAt(x-1)=='-')&&(immune>=0))
								immune=-1;
							else
							if(text.charAt(x-1)!='-')
								immune=x;
						}
					}
					break;
				default:
					break;
				}
			}
			if(immune>0) 
			{
				int lastNumber=-1;
				x=0;
				while(x<immune)
				{
					if(Character.isDigit(text.charAt(x))&&((x==0)||(!Character.isDigit(text.charAt(x-1)))))
					   lastNumber=x;
					x++;
				}
				if(lastNumber>=0)
				{
					text=text.substring(lastNumber,immune).trim();
					x=text.indexOf(" ");
					if(x>0) text=text.substring(0,x).trim();
					if(text.endsWith("%"))
						msg.setValue(msg.value()-(int)Math.round(Util.mul(msg.value(),Util.div(Util.s_int(text.substring(0,text.length()-1)),100.0))));
					else
						msg.setValue(msg.value()-Util.s_int(text));
					if(msg.value()<0) msg.setValue(0);
				}
			}
		}
		return true;
	}
}