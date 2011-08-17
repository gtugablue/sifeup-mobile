package pt.up.fe.mobile.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author Rui Araújo
 *
 */
@SuppressWarnings("serial")
public class Subject  implements Serializable{
	/** Subject code - EIC0083 */
	private String code;
	
	/** Subject Portuguese name - Arquitectura e Organização de Computadores */
	private String namePt;
	
	/** Subject English name - */
	private String nameEn;
	
	/** Subject acronym - */
	private String acronym;
	
	/** */
	private String unitName;
	
	/** */
	private String unitCode;
	
	/** */
	private String year;
	
	/** */
	private String semestre;
	
	/** */
	private List<Book> bibliography;
	
	/** */
	private List<Workload> workload;
	
	/** */
	private String content;
	
	/** */
	private String objectives;
	
	/** */
	private String metodology;
	
	/** */
	private List<EvaluationComponent> evaluation;

	/** */
	private String evaluationFormula;

	/** */
	private String frequenceCond;
	
	/** */
	private String observations;
	
	/** */
	private String evaluationProc;
	
	/** */
	private String improvementProc;
	
	/** */
	private String evaluationExams;
	
	/** */
	private List<Responsible> responsibles;
	
	/** */
	private List<WorloadDesc> worloadDesc;

	/** */
	private List<Software> software;
	
	
	public Subject(){
		responsibles  = new ArrayList<Responsible>();
		workload = new ArrayList<Workload>();
		worloadDesc = new ArrayList<WorloadDesc>();
		evaluation = new ArrayList<EvaluationComponent>();
		bibliography = new ArrayList<Book>();
		software = new ArrayList<Software>();
	}
	
	/** */
	private class Book implements Serializable{
		
		/** */
		private String type;
		
		/** */
		private String typeDescription;
		
		/** */
		private String authors;
		
		/** */
		private String title;
		
		/** */
		private String link;
		
		/** */
		private String isbn;
	}
	
	/** */
	private class Workload implements Serializable{
		
		/** */
		private String type;
		
		/** */
		private String description;
		
		/** */
		private String lenght;
	}

	
	/** */
	private class EvaluationComponent implements Serializable{
		
		/** */
		private String description;
		
		/** */
		private String descriptionEn;
		
		/** */
		private String type;
		
		/** */
		private String typeDesc;
		
		/** */
		private String length;
		
		/** */
		private String conclusionDate;
	}

	
	/** */
	private class Responsible implements Serializable {
		
		/** */
		private String code;
		
		/** */
		private String name;
		
		/** */
		private String job;
	}
	
	
	/** */
	private class WorloadDesc implements Serializable {
		
		/** */
		private String type;
		
		/** */
		private String typeDesc;
		
		/** */
		private String numClasses;
		
		/** */
		private String numHours;
		
		/** */
		private List<Teacher> teachers;
		
		private WorloadDesc(){
			teachers = new ArrayList<Teacher>();
		}
	}
	
	private class Teacher implements Serializable {
		
		/** */
		private String code;
		
		/** */
		private String name;
		
		/** */
		private String time;
	}
	
	private class Software implements Serializable {
		
		/** */
		private String description;
		
		/** */
		private String name;
		
	}
	
	/** 
	 * Subject Description Parser
	 * Stores Description of Subject in @link{SubjectDescriptionFragment}
	 * Returns true in case of correct parsing.
	 * 
	 * @param page
	 * @return Subject
	 */
    public boolean JSONSubject(String page){
    	JSONObject jObject;
		try {
			jObject = new JSONObject(page);
			
			if(jObject.has("codigo")) this.code = jObject.getString("codigo");
			if(jObject.has("nome")) this.namePt = jObject.getString("nome");
			if(jObject.has("name")) this.nameEn = jObject.getString("name");
			if(jObject.has("sigla")) this.acronym = jObject.getString("sigla");
			if(jObject.has("ano_lectivo")) this.year = jObject.getString("ano_lectivo");
			if(jObject.has("periodo")) this.semestre = jObject.getString("periodo");
			if(jObject.has("unidade_codigo")) this.unitCode = jObject.getString("unidade_codigo");
			if(jObject.has("unidade_nome")) this.unitName = jObject.getString("unidade_nome");
			
			if(jObject.has("carga_horaria")){
	    		JSONArray jArray = jObject.getJSONArray("carga_horaria");
	    		for(int i = 0; i < jArray.length(); i++){
	    			Workload work = new Workload();
	    			JSONObject jWork = jArray.getJSONObject(i);
	    			if(jWork.has("tipo")) work.type = jWork.getString("tipo");
	    			if(jWork.has("descricao")) work.description = jWork.getString("descricao");
	    			if(jWork.has("horas")) work.lenght = jWork.getString("horas");
	    			this.workload.add(work);
	    		}
			}
			if(jObject.has("ds")){
	    		JSONArray jArray = jObject.getJSONArray("ds");
	    		for(int i = 0; i < jArray.length(); i++){
	    			WorloadDesc work = new WorloadDesc();
	    			JSONObject jWork = jArray.getJSONObject(i);
	    			if(jWork.has("tipo")) work.type = jWork.getString("tipo");
	    			if(jWork.has("tipo_descricao")) work.typeDesc = jWork.getString("tipo_descricao");
	    			if(jWork.has("num_turmas")) work.numClasses = jWork.getString("num_turmas");
	    			if(jWork.has("num_horas")) work.numHours = jWork.getString("num_horas");
	    			if ( jWork.has("docentes") )
	    			{
	    				JSONArray jTeachers = jWork.getJSONArray("docentes");
	    				for(int j = 0; j < jTeachers.length(); j++){
	    					Teacher teacher = new Teacher();
	    					JSONObject jTeacher = jTeachers.getJSONObject(j);
	    					if ( jTeacher.has("doc_codigo") ) teacher.code = jTeacher.getString("doc_codigo");
	    					if ( jTeacher.has("nome") ) teacher.name = jTeacher.getString("nome");
	    					if ( jTeacher.has("horas") ) teacher.time = jTeacher.getString("horas");
	    					work.teachers.add(teacher);
	    				}
	    			}
	    			this.worloadDesc.add(work);
	    		}
			}
			if(jObject.has("responsabilidades")){
	    		JSONArray jArray = jObject.getJSONArray("responsabilidades");
	    		for(int i = 0; i < jArray.length(); i++){
	    			Responsible resp = new Responsible();
	    			JSONObject jResp = jArray.getJSONObject(i);
	    			if(jResp.has("codigo")) resp.code = jResp.getString("codigo");
	    			if(jResp.has("nome")) resp.name = jResp.getString("nome");
	    			if(jResp.has("papel")) resp.job = jResp.getString("papel");
	    			this.responsibles.add(resp);
	    		}
			}
			if(jObject.has("objectivos")) this.objectives = jObject.getString("objectivos");
			if(jObject.has("conteudo")) this.content = jObject.getString("conteudo");
			if(jObject.has("bibliografia")){
	    		JSONArray jArray = jObject.getJSONArray("bibliografia");
	    		for(int i = 0; i < jArray.length(); i++){
	    			Book book = new Book();
	    			JSONObject jBook = jArray.getJSONObject(i);
	    			if(jBook.has("tipo")) book.type = jBook.getString("tipo");
	    			if(jBook.has("tipo_descr")) book.typeDescription = jBook.getString("tipo_descr");
	    			if(jBook.has("autores")) book.authors = jBook.getString("autores");
	    			if(jBook.has("titulo")) book.title = jBook.getString("titulo");
	    			if(jBook.has("link")) book.link = jBook.getString("link");
	    			if(jBook.has("isbn")) book.isbn = jBook.getString("isbn");
	    			this.bibliography.add(book);
	    		}
			}
			
			if(jObject.has("metodologia")) this.metodology = jObject.getString("metodologia");
			if(jObject.has("software")){
	    		JSONArray jArray = jObject.getJSONArray("software");
	    		for(int i = 0; i < jArray.length(); i++){
	    			Software soft = new Software();
	    			JSONObject jSoft = jArray.getJSONObject(i);
	    			if(jSoft.has("descricao")) soft.description = jSoft.getString("descricao");
	    			if(jSoft.has("nome")) soft.name = jSoft.getString("nome");
	    			this.software.add(soft);
	    		}
			}
			if(jObject.has("comp_avaliacao")){
	    		JSONArray jArray = jObject.getJSONArray("comp_avaliacao");
	    		for(int i = 0; i < jArray.length(); i++){
	    			EvaluationComponent eval = new EvaluationComponent();
	    			JSONObject jEval = jArray.getJSONObject(i);
	    			if(jEval.has("descricao")) eval.description = jEval.getString("descricao");
	    			if(jEval.has("descricao_ing")) eval.descriptionEn = jEval.getString("descricao_ing");
	    			if(jEval.has("tipo")) eval.type = jEval.getString("tipo");
	    			if(jEval.has("tipo_descr")) eval.typeDesc = jEval.getString("tipo_descr");
	    			if(jEval.has("duracao")) eval.length = jEval.getString("duracao");
	    			if(jEval.has("data_conclusao")) eval.conclusionDate = jEval.getString("data_conclusao");
	    			this.evaluation.add(eval);
	    		}
			}
			if(jObject.has("cond_frequencia")) this.frequenceCond = jObject.getString("cond_frequencia");
			if(jObject.has("for_avaliacao")) this.evaluationFormula = jObject.getString("for_avaliacao");
			if(jObject.has("provas_avaliacao")) this.evaluationExams = jObject.getString("provas_avaliacao");
			if(jObject.has("forma_avaliacao")) this.evaluationProc = jObject.getString("forma_avaliacao");
			if(jObject.has("forma_melhoria")) this.improvementProc = jObject.getString("forma_melhoria");
			if(jObject.has("observacoes")) this.observations = jObject.getString("observacoes");

	    	
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	Log.e("JSON", "subject description not found");
    	return false;
    }

}