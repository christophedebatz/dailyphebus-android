package com.dailyphebus;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;




public class DailyPhebusActivity extends Activity {
    
	Spinner busLineSpinner = null;
	Spinner busStopSpinner = null;
	Spinner busDirectionSpinner = null;
	Spinner busHourSpinner = null;
	ProgressDialog lineLoaderDialog = null;
	ProgressDialog stopLoaderDialog = null;
	Button busDateButton = null;
	Button globalSearchButton = null;
	Button hourlySearchButton = null;
	
	ArrayAdapter<BusLine> busLineAdapter = null;
	ArrayAdapter<BusStop> busStopAdapter = null;
	ArrayList<BusStop> listBusStop = null;
	int busLineSpinnerCurrent;
	int busStopSpinnerCurrent;
	BusLine selectedLine = null;
	BusStop selectedStop = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        if (!this.isConnected()) {
        	AlertDialog.Builder noConnexionDialog = new AlertDialog.Builder(this);
        	noConnexionDialog.setTitle("Réseau indisponible");
        	noConnexionDialog.setMessage("Cette application nécessite une connexion réseau. Voulez-vous en activer une?");
        	noConnexionDialog.setIcon(android.R.drawable.ic_dialog_alert);
        	noConnexionDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			startActivity (new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        			finish();
        		}
        	});
        	noConnexionDialog.setNegativeButton("Non merci", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			finish();
        		}
        	});
        	noConnexionDialog.show();
        }
        
        else {
        
        	busLineSpinner = (Spinner) this.findViewById(R.id.spBusLine);
        	busStopSpinner = (Spinner) this.findViewById(R.id.spBusStop);
        	busDirectionSpinner = (Spinner) this.findViewById(R.id.SpBusDirection);
        	busHourSpinner = (Spinner) this.findViewById(R.id.busHourSpinner);
        	busDateButton = (Button) this.findViewById(R.id.busDateButton);
        	globalSearchButton = (Button) this.findViewById(R.id.globalSearch);
        	hourlySearchButton = (Button) this.findViewById(R.id.hourlySearch);
        
        	globalSearchButton.setEnabled(false);
        	busDateButton.setEnabled(false);
        	hourlySearchButton.setEnabled(false);
        	busHourSpinner.setEnabled(false);
        	busStopSpinner.setEnabled(false);
        	busDirectionSpinner.setEnabled(false);
        
        	SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy"); 
            busDateButton.setText(sdfDate.format(new Date()));
        	
        	lineLoaderDialog = ProgressDialog.show(DailyPhebusActivity.this, "", "Chargement des lignes de bus", true);
        	
        	final Handler handler = new Handler() {
        		public void handleMessage(Message msg) {
        			busLineSpinner.setAdapter(busLineAdapter);
        			busLineSpinnerCurrent = busLineSpinner.getSelectedItemPosition();
        			lineLoaderDialog.dismiss();
        		}
        	};
    	
        	Thread checkUpdate = new Thread() {  
        		public void run() {
        			busLineAdapter = fillBusLineSpinner();
        			handler.sendEmptyMessage(0);
        		}
        	};
        	checkUpdate.start();
        
        	busLineSpinner.setOnItemSelectedListener(busLineSpinnerListener());
        	busStopSpinner.setOnItemSelectedListener(busStopSpinnerListener());
        	busDateButton.setOnClickListener(dateTextListener());
        	hourlySearchButton.setOnClickListener(hourlySearchButtonListener());
        	globalSearchButton.setOnClickListener(globalSearchButtonListener());
        }
	}

	
	
	public OnClickListener globalSearchButtonListener () {
		OnClickListener listener  = new OnClickListener() {
			
			public void onClick(View v) {
				
				showBaseSchedulesDialog(false);
				
			}
		};
		return listener;
	}
	
	
	
	public OnClickListener hourlySearchButtonListener () {
		OnClickListener listener  = new OnClickListener() {
			
			public void onClick(View v) {
				
				showBaseSchedulesDialog(true);
				
			}
		};
		return listener;
	}
	
	
	
	public OnClickListener closeSchedules (final Dialog dialogToClose) {
		OnClickListener listener  = new OnClickListener() {
			
			public void onClick(View v) {
				
				dialogToClose.dismiss();
				
			}
		};
		return listener;
	}
	
	public OnClickListener changeHourSchedules (final Dialog dialog, final int sens) {
		OnClickListener listener  = new OnClickListener() {
			
			public void onClick(View v) {
				
				dialog.dismiss();
				busHourSpinner.setSelection((int)busHourSpinner.getSelectedItemPosition() + sens);
				showBaseSchedulesDialog(true);
				
			}
		};
		return listener;
	}



	public void showBaseSchedulesDialog (boolean hourly) {
		Dialog schedulesDialog = new Dialog(DailyPhebusActivity.this);
		schedulesDialog.setContentView(hourly ? R.layout.dialogresult : R.layout.dialogresultall);
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(schedulesDialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    lp.height = WindowManager.LayoutParams.FILL_PARENT;
	    schedulesDialog.getWindow().setAttributes(lp);
	    
		schedulesDialog.setTitle("Horaires ligne " + selectedLine.getLetter());
		
		if (hourly)
			getHourlySchedulesInDialog(schedulesDialog);
		else
			getAllSchedulesInDialog(schedulesDialog);
	}
	
	
	
	public void getAllSchedulesInDialog (final Dialog schedulesDialog) {
		Button closeSchedules = (Button) schedulesDialog.findViewById(R.id.closeSchedules);
		TableLayout tl = (TableLayout) schedulesDialog.findViewById(R.id.main_table);
		TextView infoBus = (TextView) schedulesDialog.findViewById(R.id.infoBus);
		infoBus.setGravity(1);
		closeSchedules.setOnClickListener(closeSchedules(schedulesDialog));
		
		GetAndTreatSchedules gats = new GetAndTreatSchedules();
		gats.setBusLine(selectedLine);
		short direction = (short) (busDirectionSpinner.getSelectedItemPosition() == 1 ? 2 : 1);
		String dataSchedulesDay = gats.getBusSchedules(selectedStop, (String) busDateButton.getText(), "", direction);
		
		if (dataSchedulesDay.equalsIgnoreCase(""))
			infoBus.setText("Aucun bus " + selectedLine.getLetter() + " ne passe par là ce jour...");
		else {
			infoBus.setText("Ligne " + selectedLine.getLetter() + ", direction " + selectedLine.getExtremityByDirection(direction));
		
		TableRow tr_head = new TableRow(DailyPhebusActivity.this);
		tr_head.setId(10);
		tr_head.setBackgroundColor(Color.DKGRAY);
		LayoutParams rowParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		tr_head.setLayoutParams(rowParams);
		TextView label_date = new TextView(DailyPhebusActivity.this);
		label_date.setGravity(1);
        label_date.setId(20);
        label_date.setTextColor(Color.WHITE);
        label_date.setPadding(5, 5, 5, 5);
        tr_head.addView(label_date);// add the column to the table row here

        TextView label_remaining = new TextView(DailyPhebusActivity.this);
        label_remaining.setText("HORAIRES");
        label_remaining.setId(21);// define id that must be unique
        label_remaining.setTextColor(Color.WHITE); // set the color
        label_remaining.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_remaining); // add the column to the table row here
		
        
        tl.addView(tr_head, new TableLayout.LayoutParams(
                 LayoutParams.FILL_PARENT,
                 LayoutParams.WRAP_CONTENT));
        
        Date now = new Date();
        
        String[] eachHourArray = dataSchedulesDay.split(";");

        for (int i = 0; i < eachHourArray.length; i++) {
        	
        	if (this.selectedLine.getLetter().equals("00N1") && !eachHourArray[i].contains("|"))
        		continue;
        	
        	String[] hourSchedulesArray = eachHourArray[i].split("\\|");

        	if (!this.selectedLine.getLetter().equals("00N1") && Integer.parseInt(hourSchedulesArray[0]) < 5)
        		continue;
        		
        	TableRow tr = new TableRow(DailyPhebusActivity.this);
        	
        	tr.setId(100 + i);
        	tr.setLayoutParams(rowParams);
        	
        	TextView labelHour = new TextView(DailyPhebusActivity.this);
        	labelHour.setId(200 + i);
        	labelHour.setGravity(1);
        	labelHour.setPadding(2, 0, 5, 0);
        	labelHour.setTextColor(Color.WHITE);
        	
        	if (now.getHours() == Integer.parseInt(hourSchedulesArray[0]))
        		tr.setBackgroundColor(Color.MAGENTA);
        	else {
        		if (i % 2 == 0)
        			tr.setBackgroundColor(Color.GRAY);
        	}
        	
        	labelHour.setText(hourSchedulesArray[0] + "h");
        	tr.addView(labelHour);
        	
        	TextView labelRemaining = new TextView(DailyPhebusActivity.this);
        	labelRemaining.setId(200 + i);
        	
        	String hourSchedules = "";
        	for (int j = 1; j < hourSchedulesArray.length; j++)
        		hourSchedules += (j > 1) ? ", " + hourSchedulesArray[j] : hourSchedulesArray[j];

        	labelRemaining.setText(hourSchedules);
        	labelRemaining.setTextColor(Color.WHITE);
        	tr.addView(labelRemaining);
        	
        	tl.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
        }
		}
        schedulesDialog.show();
	}
	
	
	public void getHourlySchedulesInDialog (final Dialog schedulesDialog) {		
		Button closeSchedules = (Button) schedulesDialog.findViewById(R.id.closeSchedules);
		TableLayout tl = (TableLayout) schedulesDialog.findViewById(R.id.main_table);
		TextView infoBus = (TextView) schedulesDialog.findViewById(R.id.infoBus);
		infoBus.setGravity(1);
		String justHour = busHourSpinner.getSelectedItem().toString().split(":")[0].toString();
		short direction = (short) (busDirectionSpinner.getSelectedItemPosition() == 1 ? 2 : 1);
		
		closeSchedules.setOnClickListener(closeSchedules(schedulesDialog));
		
		Button nextHour = (Button) schedulesDialog.findViewById(R.id.nextHour);
		Button prevHour = (Button) schedulesDialog.findViewById(R.id.prevHour);
		
		nextHour.setOnClickListener(changeHourSchedules(schedulesDialog, 1));
		prevHour.setOnClickListener(changeHourSchedules(schedulesDialog, -1));
		
		GetAndTreatSchedules gats = new GetAndTreatSchedules();
		gats.setBusLine(selectedLine);
		String dataSchedulesDay = gats.getBusSchedules(selectedStop, (String) busDateButton.getText(), justHour, direction);

		if (justHour.equals("05"))
			prevHour.setEnabled(false);
		if (justHour.equals("01"))
			nextHour.setEnabled(false);
		
		if (dataSchedulesDay.equals(""))
			infoBus.setText("Désolé mais aucun bus " + selectedLine.getLetter() + " ne passe à " + justHour + ":00");
		else {
			infoBus.setText("Ligne " + selectedLine.getLetter() + ", direction " + selectedLine.getExtremityByDirection(direction) + ", " + justHour + ":00");
		
		TableRow tr_head = new TableRow(DailyPhebusActivity.this);
		tr_head.setId(10);
		tr_head.setBackgroundColor(Color.DKGRAY);
		LayoutParams rowParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		tr_head.setLayoutParams(rowParams);
		TextView label_date = new TextView(DailyPhebusActivity.this);
		label_date.setGravity(1);
        label_date.setId(20);
        label_date.setText("HORAIRE");
        label_date.setTextColor(Color.WHITE);
        label_date.setPadding(5, 5, 5, 5);
        tr_head.addView(label_date);// add the column to the table row here

        TextView label_remaining = new TextView(DailyPhebusActivity.this);
        label_remaining.setText("TEMPS RESTANT");
        label_remaining.setId(21);// define id that must be unique
        label_remaining.setTextColor(Color.WHITE); // set the color
        label_remaining.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_remaining); // add the column to the table row here
		
        
        tl.addView(tr_head, new TableLayout.LayoutParams(
                 LayoutParams.FILL_PARENT,
                 LayoutParams.WRAP_CONTENT));
        
        Date now = new Date();
        boolean alreadyPassedBy = false;
        String[] dataArray = dataSchedulesDay.split("\\|");

        for (int i = 0; i < dataArray.length; i++) {
        	TableRow tr = new TableRow(DailyPhebusActivity.this);
        	
        	if (now.getHours() == Integer.parseInt(justHour) && Integer.parseInt(dataArray[i].toString()) - now.getMinutes() >= 0 && !alreadyPassedBy) {
        		tr.setBackgroundColor(Color.MAGENTA);
        		alreadyPassedBy = true;
        	}
        	else {
        		if (i % 2 == 0)
        			tr.setBackgroundColor(Color.GRAY);
        	}
        	
        	tr.setId(100 + i);
        	tr.setLayoutParams(rowParams);
        	
        	TextView labelHour = new TextView(DailyPhebusActivity.this);
        	labelHour.setId(200 + i);
        	labelHour.setGravity(1);
        	labelHour.setText(dataArray[i].toString());
        	labelHour.setPadding(2, 0, 5, 0);
        	labelHour.setTextColor(Color.WHITE);
        	tr.addView(labelHour);
        	TextView labelRemaining = new TextView(DailyPhebusActivity.this);
        	labelRemaining.setId(200 + i);
        	
        	String remainingTime = " - ";
        	if (Integer.parseInt(dataArray[i].toString()) >= (Integer)now.getMinutes() && now.getHours() == Integer.parseInt(justHour)) {
        		remainingTime = String.valueOf(Integer.parseInt(dataArray[i]) - (Integer)now.getMinutes()) + " mn";
        	}
        	
        	labelRemaining.setText(remainingTime);
        	labelRemaining.setTextColor(Color.WHITE);
        	tr.addView(labelRemaining);
        	
        	tl.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
        }
		}
        schedulesDialog.show();
	}
        	
	
	
	
	
	public OnClickListener dateTextListener () {
		OnClickListener listener  = new OnClickListener() {
		
			DatePickerDialog datePickDiag = null;
			
			public void onClick(View v) {
				Calendar cal = Calendar.getInstance();
				datePickDiag = new DatePickerDialog (DailyPhebusActivity.this, odsl, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				datePickDiag.show();
			}
        
			public OnDateSetListener odsl = new OnDateSetListener() {
				public void onDateSet(DatePicker arg0, int year, int month, int day) {
					String sday = (day <= 9) ? "0" + String.valueOf(day) : String.valueOf(day);
					String smonth = (++month <= 9) ? "0" + String.valueOf(month) : String.valueOf(month);
					String syear = (year <= 9) ? "0" + String.valueOf(year) : String.valueOf(year);
					
					busDateButton.setText(sday + "/" + smonth + "/" + syear);
				}
			};
		};
		return listener;
    }
	
	
        
    
    
	public ArrayAdapter<BusLine> fillBusLineSpinner () { 	
    	GetAndTreatSchedules gats = new GetAndTreatSchedules();
    	ArrayAdapter<BusLine> spinnerArrayAdapter = new ArrayAdapter<BusLine>(this, android.R.layout.simple_spinner_item, gats.getBusLines());
    	spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return spinnerArrayAdapter;
    }
   
	
	
	
	 public OnItemSelectedListener busLineSpinnerListener() {
		 OnItemSelectedListener listener = new OnItemSelectedListener() {
	    	public void onItemSelected (AdapterView<?> adapterView, View view, int i, long l) { 
	    		if (i == 0) {
	    			globalSearchButton.setEnabled(false);
	            	busDateButton.setEnabled(false);
	            	hourlySearchButton.setEnabled(false);
	            	busHourSpinner.setEnabled(false);
	            	busDirectionSpinner.setEnabled(false);
	    			busStopSpinner.setEnabled(false);
	    			busDirectionSpinner.setEnabled(false);
	    			return;
	    		}
	    		
	    		if (busLineSpinnerCurrent != i) {
	    			busLineSpinnerCurrent = i;
	    			busStopSpinner.setEnabled(true);
	    			busDirectionSpinner.setEnabled(true);
	                selectedLine = new BusLine(adapterView.getItemAtPosition(i).toString());
	               
		            stopLoaderDialog = ProgressDialog.show(DailyPhebusActivity.this, "", "Chargement arrêts ligne " + selectedLine.getLetter(), true);
		            
		            final Handler handler = new Handler() {
		            	public void handleMessage(Message msg) {
		            		busStopSpinner.setAdapter(busStopAdapter);
		            		busStopSpinnerCurrent = busStopSpinner.getSelectedItemPosition();
		            		stopLoaderDialog.dismiss();
		            		
		            		//sens normal
			                String[] directions = { "Vers " + selectedLine.getSecondExtremity(), 
			                					    "Vers " + selectedLine.getFirstExtremity() };
		            		busDirectionSpinner.setAdapter(fillStringSpinner(directions));
		            	}
		            };
		        	
		            Thread checkUpdate = new Thread() {  
		            	public void run() {
		            		busStopAdapter = fillBusStopSpinner(selectedLine);
		            		handler.sendEmptyMessage(0);
		            	}
		            };
		            checkUpdate.start();

	    		}
	    	} 

	        public void onNothingSelected (AdapterView<?> adapterView) {
	            return;
	        } 
	    };
		return listener;
	 }
	 
	 
	 
	public ArrayAdapter<BusStop> fillBusStopSpinner (BusLine busLine) { 	
	    GetAndTreatSchedules gats = new GetAndTreatSchedules();
	    listBusStop = gats.getBusStops(busLine);
	    ArrayAdapter<BusStop> spinnerArrayAdapter = new ArrayAdapter<BusStop>(this, android.R.layout.simple_spinner_item, listBusStop);
	    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    return spinnerArrayAdapter;
	}
		
		
		
		
		public OnItemSelectedListener busStopSpinnerListener() {
			 OnItemSelectedListener listener = new OnItemSelectedListener() {
		    	public void onItemSelected (AdapterView<?> adapterView, View view, int i, long l) { 
		    		if (i == 0) {
		    			busDateButton.setEnabled(false);
		    			busHourSpinner.setEnabled(false);
		    			globalSearchButton.setEnabled(false);
		    			hourlySearchButton.setEnabled(false);
		    			return;
		    		}
		    		
		    		if (busStopSpinnerCurrent != i) {
		    			busStopSpinnerCurrent = i;
		    			busDateButton.setEnabled(true);
		    			busHourSpinner.setEnabled(true);
		    			globalSearchButton.setEnabled(true);
		    			hourlySearchButton.setEnabled(true);
		                selectedStop = new BusStop(adapterView.getItemAtPosition(i).toString(), listBusStop.get(i).getCode());
		                
		                String[] hours = { "05:00 AM", "06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "13:00 PM", "14:00 PM", "15:00 PM", "16:00 PM", "17:00 PM", "18:00 PM", "19:00 PM", "20:00 PM", "21:00 PM", "22:00 PM", "23:00 PM", "24:00 AM", "01:00 AM" };
		                busHourSpinner.setAdapter(fillStringSpinner(hours));
		                
		                Date now = new Date();

		                switch ((int)now.getHours()) {
		                	case 2: case 3: case 4: busHourSpinner.setSelection(0); break;
		                	default: busHourSpinner.setSelection((int)now.getHours() - 5); break;
		                }
		                
		    		}//endif
		    	} 

		        public void onNothingSelected (AdapterView<?> adapterView) {
		            return;
		        } 
		    };
			return listener;
		 }
		
		

		public ArrayAdapter<CharSequence> fillStringSpinner (String[] stringArray) {
	    	ArrayAdapter<CharSequence> spinnerArrayAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, stringArray);
	    	spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        return spinnerArrayAdapter;
		}
		
		
		
		public boolean isConnected() {
		    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    return (netInfo != null && netInfo.isConnected());
		}
		
}