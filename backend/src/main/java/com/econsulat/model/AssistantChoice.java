package com.econsulat.model;

import jakarta.persistence.*;

@Entity
@Table(name = "assistant_choices")
public class AssistantChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private AssistantStep step;

    @Column(name = "choice_key", nullable = false)
    private String choiceKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_step_id")
    private AssistantStep nextStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_document_type_id")
    private DocumentType resultDocumentType;

    @Column(name = "result_summary_key")
    private String resultSummaryKey;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssistantStep getStep() {
        return step;
    }

    public void setStep(AssistantStep step) {
        this.step = step;
    }

    public String getChoiceKey() {
        return choiceKey;
    }

    public void setChoiceKey(String choiceKey) {
        this.choiceKey = choiceKey;
    }

    public AssistantStep getNextStep() {
        return nextStep;
    }

    public void setNextStep(AssistantStep nextStep) {
        this.nextStep = nextStep;
    }

    public DocumentType getResultDocumentType() {
        return resultDocumentType;
    }

    public void setResultDocumentType(DocumentType resultDocumentType) {
        this.resultDocumentType = resultDocumentType;
    }

    public String getResultSummaryKey() {
        return resultSummaryKey;
    }

    public void setResultSummaryKey(String resultSummaryKey) {
        this.resultSummaryKey = resultSummaryKey;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
