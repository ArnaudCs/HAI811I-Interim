package com.example.interim.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryRepository {
    private Map<Integer, List<String>> categories;

    public CategoryRepository() {
        categories = new HashMap<>();
        categories.put(1, createCategoryList("Administration et Secrétariat", "Administration and Secretarial"));
        categories.put(2, createCategoryList("Vente et Commerce", "Sales and Retail"));
        categories.put(3, createCategoryList("Informatique et Technologies de l'information", "Information Technology"));
        categories.put(4, createCategoryList("Finance et Comptabilité", "Finance and Accounting"));
        categories.put(5, createCategoryList("Ressources Humaines", "Human Resources"));
        categories.put(6, createCategoryList("Marketing et Communication", "Marketing and Communication"));
        categories.put(7, createCategoryList("Ingénierie et Sciences", "Engineering and Sciences"));
        categories.put(8, createCategoryList("Santé et Médecine", "Healthcare and Medicine"));
        categories.put(9, createCategoryList("Enseignement et Formation", "Education and Training"));
        categories.put(10, createCategoryList("Services à la personne", "Personal Services"));
        categories.put(11, createCategoryList("Art et Création", "Art and Design"));
        categories.put(12, createCategoryList("Tourisme et Hôtellerie", "Tourism and Hospitality"));
        categories.put(13, createCategoryList("Juridique", "Legal"));
        categories.put(14, createCategoryList("Construction et Travaux Publics", "Construction and Civil Engineering"));
        categories.put(15, createCategoryList("Transport et Logistique", "Transportation and Logistics"));
        categories.put(16, createCategoryList("Agriculture et Environnement", "Agriculture and Environment"));
        categories.put(17, createCategoryList("Médias et Divertissement", "Media and Entertainment"));
        categories.put(18, createCategoryList("Recherche et Développement", "Research and Development"));
        categories.put(19, createCategoryList("Consulting et Stratégie", "Consulting and Strategy"));
        categories.put(20, createCategoryList("Industrie Manufacturière", "Manufacturing Industry"));
        categories.put(21, createCategoryList("Gestion de Projet", "Project Management"));
        categories.put(22, createCategoryList("Service Clientèle", "Customer Service"));
        categories.put(23, createCategoryList("Design Graphique", "Graphic Design"));
        categories.put(24, createCategoryList("Développement Web", "Web Development"));
        categories.put(25, createCategoryList("Télécommunications", "Telecommunications"));
        categories.put(26, createCategoryList("Énergie et Environnement", "Energy and Environment"));
        categories.put(27, createCategoryList("Assurance", "Insurance"));
        categories.put(28, createCategoryList("Sciences Sociales", "Social Sciences"));
        categories.put(29, createCategoryList("Gestion des Opérations", "Operations Management"));
        categories.put(30, createCategoryList("Architecture", "Architecture"));
        categories.put(31, createCategoryList("Qualité et Contrôle", "Quality Assurance and Control"));
        categories.put(32, createCategoryList("Traduction et Interprétation", "Translation and Interpretation"));
        categories.put(33, createCategoryList("Éducation Physique et Sport", "Physical Education and Sports"));
        categories.put(34, createCategoryList("Design d'Intérieur", "Interior Design"));
        categories.put(35, createCategoryList("Gestion de Projet Informatique", "IT Project Management"));
        categories.put(36, createCategoryList("Audit et Compliance", "Audit and Compliance"));
        categories.put(37, createCategoryList("Développement Mobile", "Mobile Development"));
        categories.put(38, createCategoryList("Gestion des Ressources", "Resource Management"));
        categories.put(39, createCategoryList("Administration Publique", "Public Administration"));
        categories.put(40, createCategoryList("Sécurité et Protection", "Security and Protection"));
    }

    private List<String> createCategoryList(String frenchCategory, String englishCategory) {
        List<String> categoryList = new ArrayList<>();
        categoryList.add(frenchCategory);
        categoryList.add(englishCategory);
        return categoryList;
    }

    public List<String> getCategory(int id) {
        return categories.get(id);
    }

    public Map<Integer, List<String>> getCategoryMap() {
        return categories;
    }

    public int findCategoryIdByCategoryString(String categoryString) {
        for (Map.Entry<Integer, List<String>> entry : this.categories.entrySet()) {
            List<String> categoryList = entry.getValue();
            if (categoryList.contains(categoryString)) {
                return entry.getKey();
            }
        }
        return 0; // Default category ID if not found
    }

    public boolean isValidCategory(String category){
        for (Map.Entry<Integer, List<String>> entry : this.categories.entrySet()) {
            List<String> categoryList = entry.getValue();
            if (categoryList.contains(category)) {
                return true;
            }
        }
        return false;
    }

    public String getFrench(String category) {
        for (Map.Entry<Integer, List<String>> entry : this.categories.entrySet()) {
            List<String> categoryList = entry.getValue();
            if (categoryList.contains(category)) {
                return categoryList.get(0);
            }
        }
        return "Not Found";
    }

    public String getEnglish(String category) {
        for (Map.Entry<Integer, List<String>> entry : this.categories.entrySet()) {
            List<String> categoryList = entry.getValue();
            if (categoryList.contains(category)) {
                return categoryList.get(1);
            }
        }
        return "Not Found";
    }
}
