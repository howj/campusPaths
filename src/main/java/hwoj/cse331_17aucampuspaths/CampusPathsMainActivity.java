package hwoj.cse331_17aucampuspaths;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeSet;

import hw5.*;
import hw8.*;

public class CampusPathsMainActivity extends AppCompatActivity {

    DrawView view;

    Model m;

    ListView buildingsList;
    ListView endBuildingsList;

    // For drawing the starting circle
    String startName;
    boolean first = true;
    boolean notFirst = false;
    boolean startSelected = false;

    // For drawing the ending circle
    String endName;
    boolean endFirst = true;
    boolean endNotFirst = false;
    boolean endSelected = false;

    // For path
    boolean isClear = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_paths_main);

        InputStream pathsInputStream = this.getResources().openRawResource(R.raw.campus_paths);
        InputStream buildingsInputStream = this.getResources().openRawResource(R.raw.campus_buildings);

        // Buttons
        Button pressMeButton = (Button) findViewById(R.id.PressMeButton);
        Button clearButton = (Button) findViewById(R.id.clearButton);

        // View
        view = (DrawView) findViewById(R.id.imageView2);

        // The two lists the client uses
        buildingsList = (ListView) findViewById(R.id.Buildings);
        endBuildingsList = (ListView) findViewById(R.id.EndBuildings);

        // Adapters for lists

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                       android.R.layout.simple_list_item_1, new ArrayList<String>());
        ArrayAdapter<String> endAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, new ArrayList<String>());

        // Build model
        m = new Model(buildingsInputStream, pathsInputStream);

        // Sort shortNames lexigraphically
        TreeSet<String> lexicoKeys = new TreeSet<String>(m.getStructures().keySet());

        // Put shortNames from lexicoKeys into adapter
        for (String shortName : lexicoKeys) {
            adapter.add(shortName);
            endAdapter.add(shortName);
        }

        // Set Lists
        buildingsList.setAdapter(adapter);
        endBuildingsList.setAdapter(endAdapter);

        buildingsList.setOnItemClickListener(listViewItemClick);
        endBuildingsList.setOnItemClickListener(endListViewItemClick);

        // Set buttons
        pressMeButton.setOnClickListener(pressMeButtonClick);
        clearButton.setOnClickListener(clearButtonClick);
    }

    // Listener for the list that selects the building to start from
    private ListView.OnItemClickListener listViewItemClick = new ListView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
            String buildingShortName = (String) buildingsList.getItemAtPosition(position);
            String startBuildingMessage;
            boolean newBuilding;
            Coordinate2D buildingCoords = m.getCoordinates(buildingShortName);
            if (notFirst) {
                first = false;
                notFirst = false;
            }
            if (first) {
                // first = false; // set first to false;
                startBuildingMessage = buildingShortName + " selected as the starting building.";
                newBuilding = true;
                notFirst = true;
                startSelected = true;
            } else {
                // Check to see if user selected the same building again.
                if (startName.equalsIgnoreCase(buildingShortName)) {
                    startBuildingMessage = buildingShortName + " deselected as the starting building.";
                    newBuilding = false;
                    first = true; // Reset first
                    startSelected = false;
                } else {
                    // New building selected.
                    startBuildingMessage = buildingShortName + " selected as the starting building.";
                    newBuilding = true;
                }
            }
            startName = buildingShortName;
            Toast.makeText(getApplicationContext(), startBuildingMessage, Toast.LENGTH_LONG).show();
            // Toggle selection on map
            view.toggleDrawCircle(buildingCoords.getX(), buildingCoords.getY(), first, newBuilding);
        }
    };

    // Listener for the list that selects the building to end with
    private ListView.OnItemClickListener endListViewItemClick = new ListView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
            String buildingShortName = (String) buildingsList.getItemAtPosition(position);
            String startBuildingMessage;
            boolean newBuilding;
            Coordinate2D buildingCoords = m.getCoordinates(buildingShortName);
            if (endNotFirst) {
                endFirst = false;
                endNotFirst = false;
            }
            if (endFirst) {
                // first = false; // set first to false;
                startBuildingMessage = buildingShortName + " selected as the ending building.";
                newBuilding = true;
                endNotFirst = true;
                endSelected = true;
            } else {
                // Check to see if user selected the same building again.
                if (startName.equalsIgnoreCase(buildingShortName)) {
                    startBuildingMessage = buildingShortName + " deselected as the ending building.";
                    newBuilding = false;
                    endFirst = true; // Reset first
                    endSelected = false;
                } else {
                    // New building selected.
                    startBuildingMessage = buildingShortName + " selected as the ending building.";
                    newBuilding = true;
                }
            }
            endName = buildingShortName;
            Toast.makeText(getApplicationContext(), startBuildingMessage, Toast.LENGTH_LONG).show();
            // Toggle selection on map
            view.toggleDrawEndCircle(buildingCoords.getX(), buildingCoords.getY(), endFirst, newBuilding);
        }
    };

    // Listener for the button that finds the shortest path
    private View.OnClickListener pressMeButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
            if (startSelected && endSelected) {
                if (!startName.equalsIgnoreCase(endName)) {
                    if (isClear) {
                        isClear = false;
                        view.drawShortestPath(m.findShortestPath(m.getCoordinates(startName),
                                m.getCoordinates(endName)), m.getCoordinates(startName)
                                .getX(), m.getCoordinates(startName).getY());
                    } else {
                        Toast.makeText(getApplicationContext(), "Please press CLEAR ALL " +
                                "MARKINGS to clear the current " +
                                "path before finding a new path.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "You can't find a path from a" +
                            " building to itself.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please select both a building " +
                        "to start from and a building to end with.", Toast.LENGTH_LONG).show();
            }
        }
    };

    // Button that clears all markings
    private View.OnClickListener clearButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
            view.clear();
            // Reset all flags to original state
            isClear = true;
            first = true;
            notFirst = false;
            startSelected = false;
            endFirst = true;
            endNotFirst = false;
            endSelected = false;
        }
    };
}
