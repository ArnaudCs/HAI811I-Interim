package com.example.interim.offers;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.interim.CategoryRepository;
import com.example.interim.GeocodingUtils;
import com.example.interim.R;
import com.example.interim.authentication.MainActivity;
import com.example.interim.authentication.Registration;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class fragment_post_offers extends Fragment {
    private static final int PICK_JSON_FILE_REQUEST_CODE = 1;
    private Calendar today;
    private Calendar calendarStart;
    private Calendar calendarEnd;
    CategoryRepository categoryMapInstance = new CategoryRepository();

    public fragment_post_offers() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_offers, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputLayout layoutDateStartOffer = view.findViewById(R.id.dateOfferStartLayout);
        TextInputLayout layoutDateEndOffer = view.findViewById(R.id.dateOfferEndLayout);
        TextInputEditText textDateStartOffer = view.findViewById(R.id.textOfferStartDate);
        TextInputEditText textDateEndOffer = view.findViewById(R.id.textOfferEndDate);
        TextInputEditText textOfferName = view.findViewById(R.id.textOfferName);
        TextInputEditText textOfferCompanyName = view.findViewById(R.id.textOfferCompanyName);
        TextInputEditText textOfferSalary = view.findViewById(R.id.textOfferSalary);
        TextInputEditText textOfferCity = view.findViewById(R.id.textCityOffer);
        TextInputEditText textOfferWebsite = view.findViewById(R.id.textOfferWebsite);
        TextInputEditText textOfferTags = view.findViewById(R.id.textOfferTags);
        TextInputEditText textOfferLabels = view.findViewById(R.id.textOfferLabels);
        EditText textOfferDescription = view.findViewById(R.id.offerDescriptionText);
        EditText textOfferDetails = view.findViewById(R.id.offerDetailsText);
        Button addOfferButton = view.findViewById(R.id.addOfferButton);
        Button addMultipleOffers = view.findViewById(R.id.addMultipleOffers);
        Button downloadTemplate = view.findViewById(R.id.downloadTemplate);

        BottomNavigationView bottomNav = getActivity().findViewById(R.id.navbar);
        DatePickerDialog.OnDateSetListener setListener;
        Spinner categoryOfferChoice = view.findViewById(R.id.categoryOfferChoice);
        ScrollView formScrollContainer = view.findViewById(R.id.formScrollContainer);

        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(getResources().getString(R.string.chooseCat));
        String deviceLanguage = Locale.getDefault().getLanguage();
        CategoryRepository categoryMapInstance = new CategoryRepository();
        Map<Integer, List<String>> categories = categoryMapInstance.getCategoryMap();

        if (categories != null) {
            List<String> frenchCategories = new ArrayList<>();
            List<String> englishCategories = new ArrayList<>();

            for (Map.Entry<Integer, List<String>> entry : categories.entrySet()) {
                List<String> translations = entry.getValue();
                if (translations.size() >= 2) {
                    frenchCategories.add(translations.get(0));
                    englishCategories.add(translations.get(1));
                }
            }

            if (frenchCategories != null && deviceLanguage.equals("fr")) {
                spinnerArray.addAll(frenchCategories);
            } else if (englishCategories != null) {
                spinnerArray.addAll(englishCategories);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryOfferChoice.setAdapter(adapter);

        today = Calendar.getInstance();
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarStart.set(Calendar.YEAR, year);
                calendarStart.set(Calendar.MONTH, monthOfYear);
                calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (!textDateEndOffer.getText().toString().trim().equals("")) {
                    if (calendarStart.after(calendarEnd)) {
                        Toast.makeText(getActivity(), R.string.incorrectDate, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                String dateFormat = "dd/MM/yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
                textDateStartOffer.setText(simpleDateFormat.format(calendarStart.getTime()));
            }
        };
        final DatePickerDialog.OnDateSetListener dateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarEnd.set(Calendar.YEAR, year);
                calendarEnd.set(Calendar.MONTH, monthOfYear);
                calendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (!textDateStartOffer.getText().toString().trim().equals("")) {
                    if (calendarEnd.before(calendarStart)) {
                        Toast.makeText(getActivity(), R.string.incorrectDate, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                String dateFormat = "dd/MM/yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
                textDateEndOffer.setText(simpleDateFormat.format(calendarEnd.getTime()));
            }
        };

        textDateStartOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = today.get(Calendar.YEAR);
                int month = today.get(Calendar.MONTH);
                int day = today.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener1, year, month, day);

                datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
                if (!textDateEndOffer.getText().toString().trim().equals("")) {
                    datePickerDialog.getDatePicker().setMaxDate(calendarEnd.getTimeInMillis() - 1000);
                }
                else {
                    Calendar maxDate = (Calendar) today.clone();
                    int newMonth = (month + 3) % 12;
                    if (newMonth < month)
                        maxDate.set(Calendar.YEAR, year + 1);
                    maxDate.set(Calendar.MONTH, newMonth);
                    datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                }
                datePickerDialog.show();
            }
        });
        textDateEndOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year;
                int month;
                int day;
                DatePickerDialog datePickerDialog;
                if (!textDateStartOffer.getText().toString().trim().equals("")) {
                    year = calendarStart.get(Calendar.YEAR);
                    month = calendarStart.get(Calendar.MONTH);
                    day = calendarStart.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener2, year, month, day);
                    datePickerDialog.getDatePicker().setMinDate(calendarStart.getTimeInMillis() + 1000);
                }
                else {
                    year = today.get(Calendar.YEAR);
                    month = today.get(Calendar.MONTH);
                    day = today.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener2, year, month, day);
                    datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
                }
                Calendar maxDate = (Calendar) today.clone();
                int newMonth = (month + 3) % 12;
                if (newMonth < month)
                    maxDate.set(Calendar.YEAR, year + 1);
                maxDate.set(Calendar.MONTH, newMonth);
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        addMultipleOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/json");
                startActivityForResult(intent, PICK_JSON_FILE_REQUEST_CODE);
            }
        });

        addOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Get input values from views
                String jobTitle = textOfferName.getText().toString();
                String companyName = textOfferCompanyName.getText().toString();
                String salaryStr = textOfferSalary.getText().toString();
                String location = textOfferCity.getText().toString();
                String url = textOfferWebsite.getText().toString();
                String keywords = textOfferTags.getText().toString();
                String label = textOfferLabels.getText().toString();
                String description = textOfferDescription.getText().toString();
                String details = textOfferDetails.getText().toString();
                String category = categoryOfferChoice.getSelectedItem().toString();

                if (jobTitle.isEmpty() || companyName.isEmpty() || salaryStr.isEmpty() || location.isEmpty() ||
                url.isEmpty() || keywords.isEmpty() || label.isEmpty() || category.equals(getString(R.string.chooseCat))) {
                    Toast.makeText(getActivity(), R.string.emptyFields, Toast.LENGTH_SHORT).show();
                    return;
                }

                final String regexWebUrl = "[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
                final Pattern pattern = Pattern.compile(regexWebUrl);

                if (!pattern.matcher(url).matches()) {
                    Toast.makeText(getActivity(), R.string.incorrectWebsite, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Parse salary
                int salaryMin = Integer.parseInt(salaryStr), salaryMax = Integer.parseInt(salaryStr);
                if (!TextUtils.isEmpty(salaryStr)) {
                    String[] salaryParts = salaryStr.split("-");
                    if (salaryParts.length == 2) {
                        salaryMin = Integer.parseInt(salaryParts[0].trim());
                        salaryMax = Integer.parseInt(salaryParts[1].trim());
                    }
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
                Date startDate = null, endDate = null, today = null, expDate = null;
                try {
                    startDate = sdf.parse(textDateStartOffer.getText().toString());
                    endDate = sdf.parse(textDateEndOffer.getText().toString());
                    today = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(today);
                    c.add(Calendar.DATE, 30);
                    expDate = c.getTime();

                } catch (ParseException e) {
                }

                Date finalStartDate = startDate;
                Date finalEndDate = endDate;
                Date finalToday = today;
                Date finalExpDate = expDate;
                int finalSalaryMin = salaryMin;
                int finalSalaryMax = salaryMax;
                Date finalStartDate1 = startDate;
                Date finalEndDate1 = endDate;
                Date finalToday1 = today;
                Date finalExpDate1 = expDate;
                int finalSalaryMin1 = salaryMin;
                int finalSalaryMax1 = salaryMax;
                GeocodingUtils.getCoordinates(location, new GeocodingUtils.GeocodingListener() {
                    @Override
                    public void onCoordinatesObtained(double latitude, double longitude) {
                        Offer offer = new Offer(jobTitle, companyName, location, finalStartDate, finalEndDate, finalToday, finalExpDate, keywords, category, label, finalSalaryMin, finalSalaryMax, description, details, url, String.valueOf(latitude), String.valueOf(longitude));
                        offer.setRecruiter(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        db.collection("Offers")
                                .add(offer)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        textOfferName.setText("");
                                        textOfferCompanyName.setText("");
                                        textOfferSalary.setText("");
                                        textOfferCity.setText("");
                                        textOfferWebsite.setText("");
                                        textOfferTags.setText("");
                                        textOfferLabels.setText("");
                                        textOfferDescription.setText("");
                                        textOfferDetails.setText("");
                                        categoryOfferChoice.setSelection(0);
                                        layoutDateStartOffer.setError(null);
                                        layoutDateEndOffer.setError(null);
                                        textDateStartOffer.setText("");
                                        textDateEndOffer.setText("");
                                        Toast.makeText(getActivity(), R.string.offerAdded, Toast.LENGTH_SHORT).show();
                                        Intent mainActivity = new Intent(getActivity(), CelebrationActivity.class);
                                        startActivity(mainActivity);
                                        getActivity().finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Offer offer = new Offer(jobTitle, companyName, location, finalStartDate1, finalEndDate1, finalToday1, finalExpDate1, keywords, category, label, finalSalaryMin1, finalSalaryMax1, description, details, url, "0.0", "0.0");
                                        offer.setRecruiter(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        db.collection("Offers")
                                                .add(offer)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        textOfferName.setText("");
                                                        textOfferCompanyName.setText("");
                                                        textOfferSalary.setText("");
                                                        textOfferCity.setText("");
                                                        textOfferWebsite.setText("");
                                                        textOfferTags.setText("");
                                                        textOfferLabels.setText("");
                                                        textOfferDescription.setText("");
                                                        textOfferDetails.setText("");
                                                        categoryOfferChoice.setSelection(0);
                                                        layoutDateStartOffer.setError(null);
                                                        layoutDateEndOffer.setError(null);
                                                        textDateStartOffer.setText("");
                                                        textDateEndOffer.setText("");
                                                        Toast.makeText(getActivity(), R.string.offerAdded, Toast.LENGTH_SHORT).show();
                                                        Intent mainActivity = new Intent(getActivity(), CelebrationActivity.class);
                                                        startActivity(mainActivity);
                                                        getActivity().finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getActivity(), R.string.offerNotAdded, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                    }

                    @Override
                    public void onFailure() {
//                        System.out.println("Failed to obtain coordinates");
                        Toast.makeText(getActivity(), R.string.incorrectCity, Toast.LENGTH_SHORT).show();
                    }
                });




            }
        });

        downloadTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadJsonTemplate();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (requestCode == PICK_JSON_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Récupérer l'URI du fichier sélectionné
            Uri fileUri = data.getData();

            try {
                // Lire le contenu du fichier JSON
                InputStream inputStream = getContext().getContentResolver().openInputStream(fileUri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();

                // Parser le contenu JSON
                Object json = new JSONTokener(stringBuilder.toString()).nextValue();

                if (json instanceof JSONArray) {
                    // Le contenu JSON est un tableau
                    JSONArray offersArray = (JSONArray) json;
                    int offersCount = offersArray.length();

                    List<Offer> offerList = new ArrayList<>(); // Liste pour stocker les offres

                    for (int i = 0; i < offersCount; i++) {
                        JSONObject offer = offersArray.getJSONObject(i);

                        // Extraire les informations de chaque offre
                        String category = offer.optString("category");
                        String companyName = offer.optString("companyName");
                        String description = offer.optString("description");
                        String details = offer.optString("details");
                        String jobTitle = offer.optString("jobTitle");
                        String keywords = offer.optString("keywords");
                        String label = offer.optString("label");
                        String location = offer.optString("location");
                        int salaryMax = offer.optInt("salaryMax");
                        int salaryMin = offer.optInt("salaryMin");
                        String startD = offer.optString("startDate");
                        String endD = offer.optString("endDate");
                        String url = offer.optString("url");

                        if(!categoryMapInstance.isValidCategory(category)){
                            showDialogError(getString(R.string.erroCatregoryJson));
                            return;
                        }

                        // Vérifier si les éléments requis sont présents
                        if (category == "" || companyName == "" || description == "" || details == "" ||
                                jobTitle == "" || keywords == "" || label == "" || location == "" ||
                                startD == "" || endD == "" || url == "") {
                            showDialogError(getString(R.string.wrongJson));
                            return;
                        } else if (category.equals("") || companyName.equals("") || description.equals("") || details.equals("") ||
                                jobTitle.equals("") || keywords.equals("") || label.equals("") || location.equals("") ||
                                startD.equals("") || endD.equals("") || url.equals("")) {
                            showDialogError(getString(R.string.wrongJson));
                            return;
                        }

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
                        Date startDate = null, endDate = null, today = null, expDate = null;
                        try {
                            startDate = sdf.parse(startD);
                            endDate = sdf.parse(endD);
                            today = new Date();
                            Calendar c = Calendar.getInstance();
                            c.setTime(today);
                            c.add(Calendar.DATE, 30);
                            expDate = c.getTime();

                        } catch (ParseException e) {
                        }

                        if(startDate == null || endDate == null || today == null || expDate == null){
                            showDialogError(getString(R.string.erroDateJson));
                            return;
                        }

                        Offer offerObj = new Offer(jobTitle, companyName, location, startDate, endDate, today, expDate, keywords, category, label, salaryMin, salaryMax, description, details, url);
                        offerObj.setRecruiter(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        offerList.add(offerObj);
                    }


                    StringBuilder offerDetails = new StringBuilder();

                    for (Offer offer : offerList) {
                        offerDetails.append("Category: ").append(offer.getCategory()).append("\n");
                        offerDetails.append("Company Name: ").append(offer.getCompanyName()).append("\n");
                        offerDetails.append("Description: ").append(offer.getDescription()).append("\n");

                        offerDetails.append("\n");
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.offersDetailsMulti));
                    ScrollView scrollView = new ScrollView(getContext());
                    TextView textView = new TextView(getContext());
                    textView.setText(offerDetails.toString());
                    scrollView.addView(textView);
                    builder.setView(scrollView);

                    builder.setPositiveButton(getString(R.string.continueBtn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Offer offer : offerList) {
                                // Get the offer location
                                String location = offer.getLocation();

                                // Perform geocoding to obtain coordinates
                                GeocodingUtils.getCoordinates(location, new GeocodingUtils.GeocodingListener() {
                                    @Override
                                    public void onCoordinatesObtained(double latitude, double longitude) {
                                        // Update the offer with the obtained coordinates
                                        offer.setPosX(String.valueOf(latitude));
                                        offer.setPosY(String.valueOf(longitude));

                                        // Post the offer to the database
                                        db.collection("Offers")
                                                .add(offer);
                                    }

                                    @Override
                                    public void onFailure() {
                                        db.collection("Offers")
                                                .add(offer);
                                    }
                                });
                            }


                            if (offerList.size() == offerList.size()) {
                                Intent celebrationActivity = new Intent(getActivity(), CelebrationActivity.class);
                                startActivity(celebrationActivity);
                                getActivity().finish();
                            }
                        }
                    });

                    builder.setNegativeButton(getString(R.string.cancelBtn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else if (json instanceof JSONObject) { // si une seule offre dans le json
                    JSONObject offerObject = (JSONObject) json;
                    String category = offerObject.optString("category");
                    String companyName = offerObject.optString("companyName");
                    String description = offerObject.optString("description");
                    String details = offerObject.optString("details");
                    String jobTitle = offerObject.optString("jobTitle");
                    String keywords = offerObject.optString("keywords");
                    String label = offerObject.optString("label");
                    String location = offerObject.optString("location");
                    int salaryMax = offerObject.optInt("salaryMax");
                    int salaryMin = offerObject.optInt("salaryMin");
                    String startD = offerObject.optString("startDate");
                    String endD = offerObject.optString("endDate");
                    String url = offerObject.optString("url");

                    if(!categoryMapInstance.isValidCategory(category)){
                        showDialogError(getString(R.string.erroCatregoryJson));
                        return;
                    }

                    if (category == "" || companyName == "" || description == "" || details == "" ||
                            jobTitle == "" || keywords == "" || label == "" || location == "" ||
                            startD == "" || endD == "" || url == "") {
                        showDialogError("Champs requis manquants dans l'objet JSON");
                        return;
                    } else if (category.equals("") || companyName.equals("") || description.equals("") || details.equals("") ||
                            jobTitle.equals("") || keywords.equals("") || label.equals("") || location.equals("") ||
                            startD.equals("") || endD.equals("") || url.equals("")) {
                        showDialogError(getString(R.string.wrongJson));
                        return;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
                    Date startDate = null, endDate = null, today = null, expDate = null;
                    try {
                        startDate = sdf.parse(startD);
                        endDate = sdf.parse(endD);
                        today = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(today);
                        c.add(Calendar.DATE, 30);
                        expDate = c.getTime();

                    } catch (ParseException e) {

                    }

                    if(startDate == null || endDate == null || today == null || expDate == null){
                        showDialogError(getString(R.string.erroDateJson));
                        return;
                    }

                    Offer offerObj = new Offer(jobTitle, companyName, location, startDate, endDate, today, expDate, keywords, category, label, salaryMin, salaryMax, description, details, url);
                    offerObj.setRecruiter(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    StringBuilder offerDetails = new StringBuilder();
                    offerDetails.append("Catégorie : ").append(category).append("\n");
                    offerDetails.append("Nom de l'entreprise : ").append(companyName).append("\n");
                    offerDetails.append("Description : ").append(description).append("\n");

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.offerDetail));
                    builder.setMessage(offerDetails.toString());
                    builder.setPositiveButton(getString(R.string.continueBtn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String location = offerObj.getLocation();

                            // Perform geocoding to obtain coordinates
                            GeocodingUtils.getCoordinates(location, new GeocodingUtils.GeocodingListener() {
                                @Override
                                public void onCoordinatesObtained(double latitude, double longitude) {
                                    // Update the offer with the obtained coordinates
                                    offerObj.setPosX(String.valueOf(latitude));
                                    offerObj.setPosY(String.valueOf(longitude));

                                    // Post the offer to the database
                                    db.collection("Offers")
                                            .add(offerObj);
                                }
                                @Override
                                public void onFailure() {
                                    db.collection("Offers")
                                            .add(offerObj);
                                }
                            });

                            Intent mainActivity = new Intent(getActivity(), CelebrationActivity.class);
                            startActivity(mainActivity);
                            getActivity().finish();
                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancelBtn), null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    showDialogError("Invalid JSON Format");
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                showDialogError("Error parsing JSON");
            }
        }
    }

    private void showDialogError(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Erreur");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void downloadJsonTemplate() {
        String fileName = "offersTemplate.json";
        String folderPath = "jsontemplate/";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(folderPath + fileName);
        File localFile = new File(Environment.getExternalStorageDirectory().getPath(), fileName);

        storageRef.getFile(localFile)
                .addOnSuccessListener(taskSnapshot -> {
                    showDialog(getString(R.string.downloadedFileSuccess), getString(R.string.downloadSuccessText) + localFile.getPath());
                })
                .addOnFailureListener(exception -> {
                    showDialog(getString(R.string.DownloadErro), getString(R.string.downloadErrorText) + exception.getMessage());
                });
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}