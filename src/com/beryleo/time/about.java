package com.beryleo.time;
//checked for localisation issues, is clean
public class about {
	//about class
	//2012-7-23: credit given to beryleo, and to dev of standout library, and version number/name
	//2012-7-23: want to add intents to launch email when clicking on my email or standout dev email
	//2012-7-23: want to add intent to launch rating on market when clicking on the version number
	//2012-7-23: should display a popup when paging to this screen that clicking the version will rate app
	protected timeactivity context;
    //add a constructor with the Context of your activity
    public about(timeactivity _context){
        context = _context;
    }
    public void aboutmain()  {
    	//this runs on data obtained from context passed to it
    	//[this is from the main activity thread]
    	//runs on a separate thread from the main stuff
    	context.runOnUiThread(new Thread() {
            @Override
            public void run() {
            //this is just setting the font styling stuff 
            //like font and size
            	context.versiontextview.setTypeface(context.digitalfont);
            	context.versiontextview.setTextSize(context.largefont);
            }
        });
    }

}
