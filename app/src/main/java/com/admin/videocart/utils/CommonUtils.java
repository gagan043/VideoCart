package com.admin.videocart.utils;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonUtils {
	static Context ctx;
	public static int TYPE_WIFI = 1;
	public static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECTED = 0;

	static String userid = "",userStatus="0";
	
	public static boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
 
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
             
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        } 
        return false;
    }
     
    
    public static void openInternetDialog(Context c){
    	ctx = c;
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
		alertDialogBuilder.setTitle("Internet Alert!");
		alertDialogBuilder
				.setMessage("You are not connected to Internet..Please Enable Internet!")
				.setCancelable(false)
			
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								
								final Intent intent = new Intent(Intent.ACTION_MAIN, null);
								intent.addCategory(Intent.CATEGORY_LAUNCHER);
								final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
								intent.setComponent(cn);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								ctx.startActivity( intent);
							}
						})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		
		
    }
    public static String getFriendlyTime(Date dateTime) {
	    StringBuffer sb = new StringBuffer();
	    Date current = Calendar.getInstance().getTime();
	    long diffInSeconds = (current.getTime() - dateTime.getTime()) / 1000;

	    Log.e("time format 1", "::::"+diffInSeconds);
	  
	    long sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
	    long min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
	    long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
	    long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
	    long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
	    long years = (diffInSeconds = (diffInSeconds / 12));

	    if (years > 0) {
	        if (years == 1) {
	            sb.append("a year");
	        } else {
	            sb.append(years + " years");
	        }
	        if (years <= 6 && months > 0) {
	            if (months == 1) {
	                sb.append(" and a month");
	            } else {
	                sb.append(" and " + months + " months");
	            }
	        }
	    } else if (months > 0) {
	       
	          
	            sb.append(months + " months");
	       
	        if (months <= 6 && days > 0) {
	            if (days == 1) {
	                sb.append(" and a day");
	            } else {
	                sb.append("");
	            }
	        }
	    } else if (days > 0) {
	        
	            sb.append(days + " days");
	       
	      
	    } else if (hrs > 0) {
	       
	            sb.append(hrs + " hours");
	       
	        if (min > 1) {
	            sb.append("");
	        }
	    } else if (min >= 0) {
	      
	            sb.append(min + " minutes");
	      
	      
	    } else {
	        if (sec <= 1) {
	            sb.append("about a second");
	        } else {
	            sb.append("" + sec + " seconds");
	        }
	    }

	    sb.append(" ago");
	    
	    Log.e("Date time format", "::::"+sb.toString());
	    return sb.toString();
	}
    
    public static long getRemainingTime(long timeofnotification){
		long currentTime = System.currentTimeMillis();
		long A = 120000-(currentTime - timeofnotification);
    	return A;
    }
    
   



	    public static boolean isEmailValid(String email) {
		    boolean isValid = false;

		    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		    CharSequence inputStr = email;

		    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		    Matcher matcher = pattern.matcher(inputStr);
		    if (matcher.matches()) {
		        isValid = true;
		    }
		    return isValid;
		}

	public static void getListViewSize(ListView myListView) {
		ListAdapter myListAdapter = myListView.getAdapter();
		if (myListAdapter == null) {
			//do nothing return null
			return;
		}
		//set listAdapter in loop for getting final size
		int totalHeight = 0;
		for (int size = 0; size < myListAdapter.getCount(); size++) {
			View listItem = myListAdapter.getView(size, null, myListView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		//setting listview item in adapter
		ViewGroup.LayoutParams params = myListView.getLayoutParams();
		params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
		myListView.setLayoutParams(params);
		// print height of adapter on log
		Log.i("height of listItem:", String.valueOf(totalHeight));
	}


	public static void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null)
			return;
		int desiredWidth = View.MeasureSpec.makeMeasureSpec(gridView.getWidth(), View.MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		int rows = listAdapter.getCount() / columns;
		if(listAdapter.getCount() % columns> 0){
			rows++;
		}
		for (int i = 0; i < rows; i++) {
			view = listAdapter.getView(i, view, gridView);
			if (i == 0)
				view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

			view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight + (gridView.getHorizontalSpacing() * rows);
		gridView.setLayoutParams(params);
		gridView.requestLayout();

	}
	public static String cap(String name) {
		StringBuilder sb = new StringBuilder(name);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}
		
}
