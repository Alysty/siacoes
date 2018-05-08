package br.edu.utfpr.dv.siacoes.window;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import br.edu.utfpr.dv.siacoes.Session;
import br.edu.utfpr.dv.siacoes.bo.JuryAppraiserRequestBO;
import br.edu.utfpr.dv.siacoes.bo.JuryRequestBO;
import br.edu.utfpr.dv.siacoes.bo.ProposalBO;
import br.edu.utfpr.dv.siacoes.bo.SigetConfigBO;
import br.edu.utfpr.dv.siacoes.components.StageComboBox;
import br.edu.utfpr.dv.siacoes.components.SupervisorComboBox;
import br.edu.utfpr.dv.siacoes.model.JuryRequest;
import br.edu.utfpr.dv.siacoes.model.ProposalAppraiser.ProposalFeedback;
import br.edu.utfpr.dv.siacoes.util.DateUtils;
import br.edu.utfpr.dv.siacoes.model.JuryAppraiserRequest;
import br.edu.utfpr.dv.siacoes.view.ListView;

public class EditJuryRequestWindow extends EditWindow {
	
	private final JuryRequest jury;
	private final List<JuryAppraiserRequest> members;
	private final List<JuryAppraiserRequest> substitutes;
	
	private final TabSheet tabContainer;
	private final TextField textStudent;
	private final TextField textTitle;
	private final StageComboBox comboStage;
	private final DateField textDate;
	private final TextField textLocal;
	private final TextArea textComments;
	private final TextArea textSupervisorAbsenceReason;
	private final SupervisorComboBox comboChair;
	private final HorizontalLayout layoutAppraisers;
	private Grid gridAppraisers;
	private final Button buttonAddAppraiser;
	private final Button buttonRemoveAppraiser;
	private final HorizontalLayout layoutSubstitutes;
	private Grid gridSubstitutes;
	private final Button buttonAddSubstitute;
	private final Button buttonRemoveSubstitute;
	
	public EditJuryRequestWindow(JuryRequest jury, ListView parentView) {
		super("Requisição de Banca", parentView);
		
		if(jury == null){
			this.jury = new JuryRequest();
		}else{
			this.jury = jury;
		}
		this.members = new ArrayList<JuryAppraiserRequest>();
		this.substitutes = new ArrayList<JuryAppraiserRequest>();
		
		this.tabContainer = new TabSheet();
		this.tabContainer.setWidth("810px");
		this.tabContainer.setHeight("520px");
		
		this.textStudent = new TextField("Acadêmico");
		this.textStudent.setWidth("800px");
		this.textStudent.setEnabled(false);
		
		this.textTitle = new TextField("Título do Trabalho");
		this.textTitle.setWidth("800px");
		this.textTitle.setEnabled(false);
		
		this.comboStage = new StageComboBox();
		this.comboStage.setEnabled(false);
		
		this.textDate = new DateField("Data");
		this.textDate.setDateFormat("dd/MM/yyyy HH:mm");
		this.textDate.setResolution(Resolution.MINUTE);
		
		this.textLocal = new TextField("Local");
		this.textLocal.setWidth("800px");
		this.textLocal.setMaxLength(100);
		
		this.textComments = new TextArea("Observações");
		this.textComments.setWidth("800px");
		this.textComments.setHeight("150px");
		this.textComments.addStyleName("textscroll");
		
		this.textSupervisorAbsenceReason = new TextArea("Motivo da ausência do Professor Orientador na banca");
		this.textSupervisorAbsenceReason.setWidth("800px");
		this.textSupervisorAbsenceReason.setHeight("75px");
		this.textSupervisorAbsenceReason.addStyleName("textscroll");
		
		HorizontalLayout h1 = new HorizontalLayout(this.comboStage, this.textDate);
		h1.setSpacing(true);
		VerticalLayout tab1 = new VerticalLayout(this.textStudent, this.textTitle, h1, this.textLocal, this.textComments);
		tab1.setSpacing(true);
		this.tabContainer.addTab(tab1, "Informações da Banca");
		
		this.comboChair = new SupervisorComboBox("Presidente da Banca", Session.getSelectedDepartment().getDepartment().getIdDepartment(), new SigetConfigBO().getSupervisorFilter(Session.getSelectedDepartment().getDepartment().getIdDepartment()));
		this.comboChair.setWidth("800px");
		
		this.layoutAppraisers = new HorizontalLayout();
		this.layoutAppraisers.setSizeFull();
		
		this.buttonAddAppraiser = new Button("Adicionar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addAppraiser();
            }
        });
		this.buttonAddAppraiser.setWidth("100px");
		
		this.buttonRemoveAppraiser = new Button("Remover", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeAppraiser();
            }
        });
		this.buttonRemoveAppraiser.setWidth("100px");
		
		VerticalLayout v1 = new VerticalLayout(this.buttonAddAppraiser, this.buttonRemoveAppraiser);
		v1.setSpacing(true);
		v1.setWidth("100px");
		HorizontalLayout h2 = new HorizontalLayout(this.layoutAppraisers, v1);
		h2.setSpacing(true);
		h2.setMargin(true);
		h2.setExpandRatio(this.layoutAppraisers, 1f);
		h2.setHeight("120px");
		h2.setWidth("800px");
		Panel panelAppraisers = new Panel("Membros Titulares");
		panelAppraisers.setContent(h2);
		
		this.layoutSubstitutes = new HorizontalLayout();
		this.layoutSubstitutes.setSizeFull();
		
		this.buttonAddSubstitute = new Button("Adicionar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	addSubstitute();
            }
        });
		this.buttonAddSubstitute.setWidth("100px");
		
		this.buttonRemoveSubstitute = new Button("Remover", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeSubstitute();
            }
        });
		this.buttonRemoveSubstitute.setWidth("100px");
		
		VerticalLayout v2 = new VerticalLayout(this.buttonAddSubstitute, this.buttonRemoveSubstitute);
		v2.setSpacing(true);
		v2.setWidth("100px");
		HorizontalLayout h3 = new HorizontalLayout(this.layoutSubstitutes, v2);
		h3.setSpacing(true);
		h3.setMargin(true);
		h3.setExpandRatio(this.layoutSubstitutes, 1f);
		h3.setHeight("120px");
		h3.setWidth("800px");
		Panel panelSubstitutes = new Panel("Suplentes");
		panelSubstitutes.setContent(h3);
		
		VerticalLayout tab2 = new VerticalLayout(this.comboChair, panelAppraisers, panelSubstitutes, this.textSupervisorAbsenceReason);
		tab2.setSpacing(true);
		this.tabContainer.addTab(tab2, "Membros");
		
		this.addField(this.tabContainer);
		
		this.loadJury();
	}
	
	private void loadJury() {
		this.textStudent.setValue(this.jury.getStudent());
		this.textTitle.setValue(this.jury.getTitle());
		this.textDate.setValue(this.jury.getDate());
		this.textLocal.setValue(this.jury.getLocal());
		this.textComments.setValue(this.jury.getComments());
		this.comboStage.setStage(this.jury.getStage());
		this.textSupervisorAbsenceReason.setValue(this.jury.getSupervisorAbsenceReason());
		
		if(this.jury.getAppraisers() == null){
			try {
				JuryAppraiserRequestBO bo = new JuryAppraiserRequestBO();
				
				this.jury.setAppraisers(bo.listAppraisers(this.jury.getIdJuryRequest()));
			} catch (Exception e) {
				this.jury.setAppraisers(null);
				
				Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
				
				Notification.show("Carregar Banca", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		}
		
		for(JuryAppraiserRequest appraiser : this.jury.getAppraisers()) {
			if(appraiser.isChair()) {
				this.comboChair.setProfessor(appraiser.getAppraiser());
			} else if(appraiser.isSubstitute()) {
				this.substitutes.add(appraiser);
			} else {
				this.members.add(appraiser);
			}
		}
		
		this.loadGridAppraisers();
		this.loadGridSubstitutes();
		
		if(this.jury.isConfirmed()) {
			this.setSaveButtonEnabled(false);
		}
	}

	@Override
	public void save() {
		try{
			JuryRequestBO bo = new JuryRequestBO();
			
			this.jury.setLocal(this.textLocal.getValue());
			this.jury.setComments(this.textComments.getValue());
			this.jury.setDate(this.textDate.getValue());
			this.jury.setComments(this.textComments.getValue());
			this.jury.setSupervisorAbsenceReason(this.textSupervisorAbsenceReason.getValue());
			
			bo.save(this.jury);
			
			this.showReport(bo.getJuryRequestForm(this.jury.getIdJuryRequest()));
			
			Notification.show("Salvar Agendamento de Banca", "Agendamento de banca salvo com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
			
			this.parentViewRefreshGrid();
			this.close();
		}catch(Exception e){
			Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e);
			
			Notification.show("Salvar Agendamento de Banca", e.getMessage(), Notification.Type.ERROR_MESSAGE);
		}
	}
	
	private void loadGridAppraisers() {
		this.gridAppraisers = new Grid();
		this.gridAppraisers.addColumn("Membro", String.class);
		this.gridAppraisers.addColumn("Nome", String.class);
		this.gridAppraisers.setSizeFull();
		this.gridAppraisers.getColumns().get(0).setWidth(100);
		
		if(this.jury.getAppraisers() != null) {
			int member = 1;
			
			for(JuryAppraiserRequest appraiser : this.members) {
				if(!appraiser.isSubstitute() && !appraiser.isChair()) {
					this.gridAppraisers.addRow("Membro " + String.valueOf(member), appraiser.getAppraiser().getName());
					member = member + 1;
				}
			}
		}
		
		this.layoutAppraisers.removeAllComponents();
		this.layoutAppraisers.addComponent(this.gridAppraisers);
	}
	
	private void loadGridSubstitutes() {
		this.gridSubstitutes = new Grid();
		this.gridSubstitutes.addColumn("Suplente", String.class);
		this.gridSubstitutes.addColumn("Nome", String.class);
		this.gridSubstitutes.setSizeFull();
		this.gridSubstitutes.getColumns().get(0).setWidth(100);
		
		if(this.jury.getAppraisers() != null) {
			int member = 1;
			
			for(JuryAppraiserRequest appraiser : this.substitutes) {
				if(appraiser.isSubstitute()) {
					this.gridSubstitutes.addRow("Suplente " + String.valueOf(member), appraiser.getAppraiser().getName());
					member = member + 1;
				}
			}
		}
		
		this.layoutSubstitutes.removeAllComponents();
		this.layoutSubstitutes.addComponent(this.gridSubstitutes);
	}
	
	private void addAppraiser() {
		UI.getCurrent().addWindow(new EditJuryAppraiserWindow(this, false));
	}
	
	public void addAppraiser(JuryAppraiserRequest appraiser) throws Exception {
		JuryRequestBO bo = new JuryRequestBO();
		
		if(bo.canAddAppraiser(this.jury, appraiser.getAppraiser())) {
			this.jury.getAppraisers().add(appraiser);
			
			if(appraiser.isSubstitute()) {
				this.substitutes.add(appraiser);
			} else {
				this.members.add(appraiser);
			}
			
			this.loadGridAppraisers();
			this.loadGridSubstitutes();
		}
	}
	
	private void removeAppraiser() {
		int index = this.getAppraiserSelectedIndex();
		
		if(index == -1){
			Notification.show("Selecionar Membro", "Selecione o membro para remover.", Notification.Type.WARNING_MESSAGE);
		}else{
			ConfirmDialog.show(UI.getCurrent(), "Confirma a remoção do membro?", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	for(int i = 0; i < jury.getAppraisers().size(); i++) {
                    		if(jury.getAppraisers().get(i).getAppraiser().getIdUser() == members.get(index).getAppraiser().getIdUser()) {
                    			jury.getAppraisers().remove(i);
                    			break;
                    		}
                    	}
                    	
                    	members.remove(index);
                    	loadGridAppraisers();
                    }
                }
            });
		}
	}
	
	private int getAppraiserSelectedIndex() {
    	Object itemId = this.gridAppraisers.getSelectedRow();

    	if(itemId == null){
    		return -1;
    	}else{
    		return ((int)itemId) - 1;	
    	}
    }
	
	private void addSubstitute() {
		UI.getCurrent().addWindow(new EditJuryAppraiserWindow(this, true));
	}
	
	private void removeSubstitute() {
		int index = this.getSubstituteSelectedIndex();
		
		if(index == -1){
			Notification.show("Selecionar Suplente", "Selecione o suplente para remover.", Notification.Type.WARNING_MESSAGE);
		}else{
			ConfirmDialog.show(UI.getCurrent(), "Confirma a remoção do suplente?", new ConfirmDialog.Listener() {
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	for(int i = 0; i < jury.getAppraisers().size(); i++) {
                    		if(jury.getAppraisers().get(i).getAppraiser().getIdUser() == substitutes.get(index).getAppraiser().getIdUser()) {
                    			jury.getAppraisers().remove(i);
                    			break;
                    		}
                    	}
                    	
                    	substitutes.remove(index);
                    	loadGridSubstitutes();
                    }
                }
            });
		}
	}
	
	private int getSubstituteSelectedIndex() {
    	Object itemId = this.gridSubstitutes.getSelectedRow();

    	if(itemId == null){
    		return -1;
    	}else{
    		return ((int)itemId) - 1;	
    	}
    }

}