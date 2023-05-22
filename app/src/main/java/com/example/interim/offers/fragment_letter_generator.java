package com.example.interim.offers;

import static androidx.core.content.res.ResourcesCompat.getDrawable;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.interim.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.itextpdf.layout.Document;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.itextpdf.layout.element.Image;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class fragment_letter_generator extends Fragment {

    Button backBtnGenerator;

    String objectFrench;
    Boolean french;
    Boolean male;

    Spinner styleChooser;

    ImageSlider doc_slider;
    TextInputEditText name, firstName, companyName, phoneNumber, mail, adress, companyAdress, companyCity;
    CheckBox maleCheck, femaleCheck;

    private static final int REQUEST_CODE = 42;
    private static final int CREATE_PDF_REQUEST_CODE = 1;

    public fragment_letter_generator() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_letter_generator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtnGenerator = view.findViewById(R.id.backBtnGenerator);
        name = view.findViewById(R.id.textApplicantName);
        firstName = view.findViewById(R.id.textApplicantFirstName);
        companyName = view.findViewById(R.id.textCompanyName);
        phoneNumber = view.findViewById(R.id.textApplicantPhone);
        mail = view.findViewById(R.id.textApplicantMail);
        adress = view.findViewById(R.id.textApplicantAdress);
        companyAdress = view.findViewById(R.id.textCompanyAdress);
        companyCity = view.findViewById(R.id.textCompanyCity);
        Button generatePdf = view.findViewById(R.id.generateBtn);
        maleCheck = view.findViewById(R.id.maleCheck);
        femaleCheck = view.findViewById(R.id.femaleCheck);
        doc_slider = view.findViewById(R.id.doc_slider);
        styleChooser = view.findViewById(R.id.styleChooser);

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add(getResources().getString(R.string.chooseStyle));
        spinnerArray.add("Style 1");
        spinnerArray.add("Style 2");
        spinnerArray.add("Style 3");
        spinnerArray.add("Style 4");
        spinnerArray.add("Style 5");
        spinnerArray.add("Style 6");
        spinnerArray.add("Style 7");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        styleChooser.setAdapter(adapter);

        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel("https://i.ibb.co/Dk1gY6w/style1.png", "Style 1", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://i.ibb.co/0V4h9R6/style2.png", "Style 2", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://i.ibb.co/bzw1t8x/style3.png", "Style 3", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://i.ibb.co/x7wgGTD/style4.png", "Style 4", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://i.ibb.co/B3jrLKS/style5.png", "Style 5", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://i.ibb.co/PmWxGrc/style6.png", "Style 6", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://i.ibb.co/xD8Dxgt/style7.png", "Style 7", ScaleTypes.FIT));

        doc_slider.setImageList(slideModels);


        maleCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            male = isChecked;
            femaleCheck.setChecked(!isChecked);
        });

        femaleCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            male = !isChecked;
            maleCheck.setChecked(!isChecked);
        });

        backBtnGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        generatePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(name.getText()) && !TextUtils.isEmpty(firstName.getText()) && !TextUtils.isEmpty(mail.getText()) &&
                        !TextUtils.isEmpty(companyName.getText()) && !TextUtils.isEmpty(companyAdress.getText()) && !TextUtils.isEmpty(companyCity.getText()) &&
                        !TextUtils.isEmpty(phoneNumber.getText()) && !TextUtils.isEmpty(adress.getText()) && (maleCheck.isChecked() || femaleCheck.isChecked()) &&
                        (styleChooser.getSelectedItemPosition() != 0)) {

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                    } else {
                        try {
                            createPdf();
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.missingFieldsErroToast), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createPdf() throws FileNotFoundException {
        String pdfName = name.getText().toString() + "_" + firstName.getText().toString() + "_coverLetter.pdf";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, pdfName);
        startActivityForResult(intent, CREATE_PDF_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    // Créer le document PDF
                    PdfWriter writer = new PdfWriter(outputStream);
                    PdfDocument pdfDocument = new PdfDocument(writer);
                    Document document = new Document(pdfDocument);

                    int choice = styleChooser.getSelectedItemPosition();

                    Drawable d = null;

                    document.setMargins(0, 36, 36, 36);

                    if (choice == 1) {
                        document.setMargins(0, 36, 36, 36);
                        d = ResourcesCompat.getDrawable(getResources(), R.drawable.header1, null);
                    } else if (choice == 2) {
                        document.setMargins(0, 36, 36, 36);
                        d = ResourcesCompat.getDrawable(getResources(), R.drawable.header2, null);
                    } else if (choice == 3) {
                        document.setMargins(0, 36, 36, 36);
                        d = ResourcesCompat.getDrawable(getResources(), R.drawable.header3, null);
                    } else if (choice == 4) {
                        document.setMargins(0, 36, 36, 36);
                        d = ResourcesCompat.getDrawable(getResources(), R.drawable.header4, null);
                    } else if (choice == 5) {
                        d = ResourcesCompat.getDrawable(getResources(), R.drawable.header5, null);
                    } else if (choice == 6) {
                        document.setMargins(36, 36, 36, 36);
                        d = ResourcesCompat.getDrawable(getResources(), R.drawable.header6, null);
                    } else if (choice == 7) {
                        document.setMargins(36, 36, 36, 36);
                        d = ResourcesCompat.getDrawable(getResources(), R.drawable.header7, null);
                    }


                    Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bitmapData = stream.toByteArray();

                    ImageData imageData = ImageDataFactory.create(bitmapData);
                    Image image = new Image(imageData);

                    // Ajouter le contenu au document
                    document.add(image).setTextAlignment(TextAlignment.RIGHT);
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(name.getText().toString() + " " + firstName.getText().toString())).setFontSize(11);
                    document.add(new Paragraph(adress.getText().toString())).setFontSize(11);
                    document.add(new Paragraph(phoneNumber.getText().toString())).setFontSize(11);
                    document.add(new Paragraph(mail.getText().toString())).setFontSize(11);

                    Calendar cal = Calendar.getInstance();
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int month = cal.get(Calendar.MONTH) + 1; // Les mois commencent à 0
                    int year = cal.get(Calendar.YEAR);
                    String dateStr = "Date : " + day + "/" + month + "/" + year;
                    document.add(new Paragraph(dateStr)).setFontSize(11).setTextAlignment(TextAlignment.LEFT);

                    document.add(new Paragraph(" "));

                    document.add(new Paragraph(companyName.getText().toString())).setFontSize(12);
                    document.add(new Paragraph(companyAdress.getText().toString())).setFontSize(12);
                    document.add(new Paragraph(companyCity.getText().toString())).setFontSize(12);

                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    String deviceLanguage = Locale.getDefault().getLanguage();

                    if (male) {
                        if (deviceLanguage.equals("fr")) {
                            document.add(new Paragraph("Objet : Candidature"));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Madame, Monsieur, "));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Je vous écris pour vous proposer ma candidature pour le poste au sein de votre entreprise. " +
                                    "J'ai été séduit par la culture d'entreprise, les valeurs et les projets que vous portez. " +
                                    "Je suis convaincu que je pourrais apporter une contribution significative à votre entreprise."));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Durant mes études j'ai développé des compétences utiles à mon parcours et mes projets. J'ai également acquis de l'expérience professionnelle " +
                                    "en travaillant dans différentes entreprises.  J'ai été attirée par votre entreprise car elle est reconnue pour sa fiabilité. \n" +
                                    "Je suis particulièrement intéressé par les projets sur lesquels vous travaillez actuellement. " +
                                    "Je suis convaincu que mes compétences et mon expérience peuvent contribuer à la réalisation de ces projets.\n"));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("En outre, je suis certain que mon attitude positive, mon esprit d'équipe et ma capacité à apprendre " +
                                    "rapidement peuvent me permettre de m'intégrer rapidement dans votre entreprise et de contribuer à votre succès.  "));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Je suis disponible pour un entretien à votre convenance pour discuter de ma candidature. Je vous remercie pour " +
                                    "l'attention que vous portez à ma candidature et j'attends votre réponse avec impatience. "));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Dans l'attente de votre réponse, veuillez agréer Madame, Monsieur, mes sincères salutations."));
                        } else {

                            document.add(new Paragraph("Object : Application"));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Dear Sir/Madam,"));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("I am writing to offer my candidacy for the position within your company. I was impressed by the company culture, values, and projects you carry. " +
                                    "I am convinced that I could make a significant contribution to your company.  "));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("During my studies, I developed skills useful to my career and projects. I also gained professional experience by working " +
                                    "in different companies. I was attracted to your company because it is known for its reliability. " +
                                    "I am particularly interested in the projects you are currently working on. " +
                                    "I am convinced that my skills and experience can contribute to the completion of these projects.  "));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Furthermore, I am certain that my positive attitude, team spirit, and ability to learn quickly can allow me to integrate quickly into your company " +
                                    "and contribute to your success.  I am available for an interview at your convenience to discuss my application. "));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Thank you for your consideration of my application and I look forward to your response."));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Best regards,"));
                        }
                    } else {
                        if (deviceLanguage.equals("fr")) {

                            document.add(new Paragraph("Objet : Candidature"));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Madame, Monsieur, "));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Je vous écris pour vous proposer ma candidature pour le poste au sein de votre entreprise. " +
                                    "J'ai été séduite par la culture d'entreprise, les valeurs et les " +
                                    "projets que vous portez. Je suis convaincue que je pourrais apporter une contribution significative à votre entreprise.  "));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Durant mes études j'ai développé des compétences utiles à mon parcours et mes projets. J'ai également acquis de " +
                                    "l'expérience professionnelle en travaillant dans différentes entreprises.  J'ai été attirée par votre entreprise car " +
                                    "elle est reconnue pour sa fiabilité. Je suis particulièrement intéressée par les projets sur lesquels vous travaillez " +
                                    "actuellement. Je suis convaincue que mes compétences et mon expérience peuvent contribuer à la réalisation de ces projets.  "));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("En outre, je suis certaine que mon attitude positive, mon esprit d'équipe et ma capacité à apprendre rapidement peuvent " +
                                    "me permettre de m'intégrer rapidement dans votre entreprise et de contribuer à votre succès.  "));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Je suis disponible pour un entretien à votre convenance pour discuter de ma candidature. Je vous remercie pour l'attention que " +
                                    "vous portez à ma candidature et j'attends votre réponse avec impatience. "));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Dans l'attente de votre réponse, veuillez agréer Madame, Monsieur, mes sincères salutations."));
                        } else {
                            document.add(new Paragraph("Object : Application"));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Dear Sir/Madam,"));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("I am writing to offer my candidacy for the position within your company. I was impressed by the corporate culture, values, and projects that you carry. " +
                                    "I am convinced that I could make a significant contribution to your company."));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("During my studies, I developed skills useful for my career and projects. I also gained professional experience by working in various companies. I was drawn " +
                                    "to your company because it is known for its reliability. "));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("I am particularly interested in the projects you are currently working on. I am convinced that my skills and experience can contribute " +
                                    "to the realization of these projects."));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Furthermore, I am certain that my positive attitude, team spirit, and ability to learn quickly can allow me to integrate quickly into your " +
                                    "company and contribute to your success."));

                            document.add(new Paragraph(" "));

                            document.add(new Paragraph("Best regards,"));
                        }
                    }

                    // Fermer le document PDF
                    document.close();

                    // Lancer le partage du fichier PDF
                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.setType("application/pdf");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Envoyer la lettre de motivation"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}