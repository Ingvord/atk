/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package fr.esrf.tangoatk.widget.util.interlock.shape;
/* Class generated by JDraw */

import java.awt.*;

/** ---------- NetDevice3 class ---------- */
public class NetDevice3 {

  private static int[][] xPolys = null;
  private static int[][] yPolys = null;

  private static Color sColor0 = new Color(153,153,153);
  private static Color sColor1 = new Color(153,204,0);
  private static Color sColor2 = new Color(102,153,0);

  private static int[][] xOrgPolys = {
    {-31,-31,21,21},
    {-31,-19,30,21},
    {21,21,30,30},
    {-29,-29,-25,-25},
    {-24,-24,-20,-20},
    {-19,-19,-15,-15},
    {-14,-14,-10,-10},
    {-9,-9,-5,-5},
    {-4,-4,0,0},
    {-29,-29,-25,-25},
    {-24,-24,-20,-20},
    {-19,-19,-15,-15},
    {-14,-14,-10,-10},
    {-9,-9,-5,-5},
    {-4,-4,0,0},
    {1,1,5,5},
    {6,6,10,10},
    {1,1,5,5},
    {6,6,10,10},
    {13,13,17,17},
    {13,13,17,17},
  };

  private static int[][] yOrgPolys = {
    {-5,10,10,-5},
    {-5,-11,-11,-5},
    {-5,10,1,-11},
    {-2,2,2,-2},
    {-2,2,2,-2},
    {-2,2,2,-2},
    {-2,2,2,-2},
    {-2,2,2,-2},
    {-2,2,2,-2},
    {3,7,7,3},
    {3,7,7,3},
    {3,7,7,3},
    {3,7,7,3},
    {3,7,7,3},
    {3,7,7,3},
    {-2,2,2,-2},
    {-2,2,2,-2},
    {3,7,7,3},
    {3,7,7,3},
    {-2,2,2,-2},
    {4,8,8,4},
  };

  static public void paint(Graphics g,Color backColor,int x,int y,double size) {

    // Allocate array once
    if( xPolys == null ) {
      xPolys = new int [xOrgPolys.length][];
      yPolys = new int [yOrgPolys.length][];
      for( int i=0 ; i<xOrgPolys.length ; i++ ) {
        xPolys[i] = new int [xOrgPolys[i].length];
        yPolys[i] = new int [yOrgPolys[i].length];
      }
    }

    // Scale and translate poly
    for( int i=0 ; i<xOrgPolys.length ; i++ ) {
      for( int j=0 ; j<xOrgPolys[i].length ; j++ ) {
        xPolys[i][j] = (int)((double)xOrgPolys[i][j]*size+0.5) + x;
        yPolys[i][j] = (int)((double)yOrgPolys[i][j]*size+0.5) + y;
      }
    }

    // Paint object
    g.setColor(backColor);g.fillPolygon(xPolys[0],yPolys[0],xPolys[0].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[0],yPolys[0],xPolys[0].length);
    g.setColor(backColor);g.fillPolygon(xPolys[1],yPolys[1],xPolys[1].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[1],yPolys[1],xPolys[1].length);
    g.setColor(backColor);g.fillPolygon(xPolys[2],yPolys[2],xPolys[2].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[2],yPolys[2],xPolys[2].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[3],yPolys[3],xPolys[3].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[3],yPolys[3],xPolys[3].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[4],yPolys[4],xPolys[4].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[4],yPolys[4],xPolys[4].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[5],yPolys[5],xPolys[5].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[5],yPolys[5],xPolys[5].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[6],yPolys[6],xPolys[6].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[6],yPolys[6],xPolys[6].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[7],yPolys[7],xPolys[7].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[7],yPolys[7],xPolys[7].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[8],yPolys[8],xPolys[8].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[8],yPolys[8],xPolys[8].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[9],yPolys[9],xPolys[9].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[9],yPolys[9],xPolys[9].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[10],yPolys[10],xPolys[10].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[10],yPolys[10],xPolys[10].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[11],yPolys[11],xPolys[11].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[11],yPolys[11],xPolys[11].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[12],yPolys[12],xPolys[12].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[12],yPolys[12],xPolys[12].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[13],yPolys[13],xPolys[13].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[13],yPolys[13],xPolys[13].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[14],yPolys[14],xPolys[14].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[14],yPolys[14],xPolys[14].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[15],yPolys[15],xPolys[15].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[15],yPolys[15],xPolys[15].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[16],yPolys[16],xPolys[16].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[16],yPolys[16],xPolys[16].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[17],yPolys[17],xPolys[17].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[17],yPolys[17],xPolys[17].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[18],yPolys[18],xPolys[18].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[18],yPolys[18],xPolys[18].length);
    g.setColor(sColor1);g.fillPolygon(xPolys[19],yPolys[19],xPolys[19].length);
    g.setColor(sColor2);g.drawPolygon(xPolys[19],yPolys[19],xPolys[19].length);
    g.setColor(sColor1);g.fillPolygon(xPolys[20],yPolys[20],xPolys[20].length);
    g.setColor(sColor2);g.drawPolygon(xPolys[20],yPolys[20],xPolys[20].length);

  }

  static public void setBoundRect(int x,int y,double size,Rectangle bound) {
    bound.setRect((int)(-31.0*size+0.5)+x,(int)(-11.0*size+0.5)+y,(int)(62.0*size+0.5),(int)(22.0*size+0.5));
  }

}

