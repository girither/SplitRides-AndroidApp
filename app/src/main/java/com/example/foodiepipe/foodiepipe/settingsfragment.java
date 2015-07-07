package com.example.foodiepipe.foodiepipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;


public class settingsfragment extends Fragment {


    private SeekBar seekBar_source;
    private SeekBar seekBar_destination;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settingsfragment,container, false);
        seekBar_source = (SeekBar)rootView.findViewById(R.id.seekBar_source);
        seekBar_destination = (SeekBar)rootView.findViewById(R.id.seekBar_destination);
        seekBar_source.setProgress(SharedPreferenceManager.getIntPreference("source_search_radius"));
        seekBar_destination.setProgress(SharedPreferenceManager.getIntPreference("destination_search_radius"));
        seekBar_source.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                      int progress = 0;
                                                      @Override
                                                      public void onStopTrackingTouch(SeekBar seekBar) {
                                                         SharedPreferenceManager.setPreference("source_search_radius",progress);
                                                          //textView.setText(progress + "/" + seekBar.getMax());
                                                      }

                                                      @Override
                                                      public void onStartTrackingTouch(SeekBar seekBar) {

                                                      }

                                                      @Override
                                                      public void onProgressChanged(SeekBar seekBar, int progressValue,boolean fromUser) {
                                                          progress = progressValue;
                                                      }

                                                  }
            );
        seekBar_destination.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                           int progress = 0;
                                                      @Override
                                                      public void onStopTrackingTouch(SeekBar seekBar) {
                                                          SharedPreferenceManager.setPreference("destination_search_radius",progress);
                                                      }

                                                      @Override
                                                      public void onStartTrackingTouch(SeekBar seekBar) {

                                                      }

                                                      @Override
                                                      public void onProgressChanged(SeekBar seekBar, int progressValue,boolean fromUser) {
                                                          progress = progressValue;

                                                      }

                                                  }
        );
        return rootView;
    }

}
