﻿package br.edu.utfpr.dv.siacoes.window;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.ui.Notification;

import br.edu.utfpr.dv.siacoes.components.StudentComboBox;

public class EditJuryParticipantWindow extends EditWindow {
	
	private final EditJuryWindow parentWindow;
	
	private final StudentComboBox comboStudent;
	
	public EditJuryParticipantWindow(EditJuryWindow parentWindow) {
		super("Adicionar Acadêmico", null);
		
		this.parentWindow = parentWindow;
		
		this.comboStudent = new StudentComboBox("Acadêmico");
		
		this.addField(this.comboStudent);
	}

	@Override
	public void save() {
		try{
			if((this.comboStudent.getStudent() == null) || (this.comboStudent.getStudent().getIdUser() == 0)){
				throw new Exception("Selecione o acadêmico.");
			}
			
			this.parentWindow.addParticipant(this.comboStudent.getStudent());
			
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Adicionar Acadêmico", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}

}