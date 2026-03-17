-- Données initiales FAQ (clés i18n côté frontend)
INSERT INTO faq_categories (code, display_order) VALUES
  ('doc_type', 1),
  ('demarches', 2),
  ('paiement_delais', 3)
ON CONFLICT (code) DO NOTHING;

INSERT INTO faq_entries (category_id, question_key, answer_key, keywords, display_order)
SELECT c.id, 'aide.faq.q1', 'aide.faq.a1', 'mariage portugal document', 1 FROM faq_categories c WHERE c.code = 'doc_type'
UNION ALL
SELECT c.id, 'aide.faq.q2', 'aide.faq.a2', 'acte naissance intégral extrait', 2 FROM faq_categories c WHERE c.code = 'doc_type'
UNION ALL
SELECT c.id, 'aide.faq.q3', 'aide.faq.a3', 'certificat mariage document', 3 FROM faq_categories c WHERE c.code = 'doc_type'
UNION ALL
SELECT c.id, 'aide.faq.q4', 'aide.faq.a4', 'délai délivrance délais', 1 FROM faq_categories c WHERE c.code = 'demarches'
UNION ALL
SELECT c.id, 'aide.faq.q5', 'aide.faq.a5', 'demande rejetée refus', 2 FROM faq_categories c WHERE c.code = 'demarches'
UNION ALL
SELECT c.id, 'aide.faq.q6', 'aide.faq.a6', 'paiement carte options', 1 FROM faq_categories c WHERE c.code = 'paiement_delais'
UNION ALL
SELECT c.id, 'aide.faq.q7', 'aide.faq.a7', 'frais coût montant', 2 FROM faq_categories c WHERE c.code = 'paiement_delais';

-- Assistant: étapes (step_id logique pour le frontend)
INSERT INTO assistant_steps (step_id, question_key, display_order) VALUES
  ('need', 'aide.assistant.need.question', 1),
  ('doc_marriage', 'aide.assistant.doc_marriage.question', 2),
  ('doc_child', 'aide.assistant.doc_child.question', 3),
  ('doc_identity', 'aide.assistant.doc_identity.question', 4),
  ('doc_other', 'aide.assistant.doc_other.question', 5);

-- Choix: need -> doc_marriage, doc_child, doc_identity, doc_other (NULL castés en INTEGER pour result_document_type_id)
INSERT INTO assistant_choices (step_id, choice_key, next_step_id, result_document_type_id, result_summary_key, display_order)
SELECT s.id, 'aide.assistant.need.marriage', n.id, CAST(NULL AS INTEGER), CAST(NULL AS VARCHAR(255)), 1
FROM assistant_steps s, assistant_steps n WHERE s.step_id = 'need' AND n.step_id = 'doc_marriage'
UNION ALL
SELECT s.id, 'aide.assistant.need.child', n.id, CAST(NULL AS INTEGER), CAST(NULL AS VARCHAR(255)), 2
FROM assistant_steps s, assistant_steps n WHERE s.step_id = 'need' AND n.step_id = 'doc_child'
UNION ALL
SELECT s.id, 'aide.assistant.need.identity', n.id, CAST(NULL AS INTEGER), CAST(NULL AS VARCHAR(255)), 3
FROM assistant_steps s, assistant_steps n WHERE s.step_id = 'need' AND n.step_id = 'doc_identity'
UNION ALL
SELECT s.id, 'aide.assistant.need.other', n.id, CAST(NULL AS INTEGER), CAST(NULL AS VARCHAR(255)), 4
FROM assistant_steps s, assistant_steps n WHERE s.step_id = 'need' AND n.step_id = 'doc_other';

-- doc_marriage -> Certificat de mariage ou Acte de naissance (référence par libellé)
INSERT INTO assistant_choices (step_id, choice_key, next_step_id, result_document_type_id, result_summary_key, display_order)
SELECT s.id, 'aide.assistant.doc_marriage.certificat', CAST(NULL AS INTEGER), (SELECT id FROM document_types WHERE libelle = 'Certificat de mariage' LIMIT 1), 'aide.assistant.result.certificat_mariage', 1
FROM assistant_steps s WHERE s.step_id = 'doc_marriage'
UNION ALL
SELECT s.id, 'aide.assistant.doc_marriage.acte', CAST(NULL AS INTEGER), (SELECT id FROM document_types WHERE libelle = 'Acte de naissance' LIMIT 1), 'aide.assistant.result.acte_naissance', 2
FROM assistant_steps s WHERE s.step_id = 'doc_marriage';

-- doc_child -> Acte de naissance
INSERT INTO assistant_choices (step_id, choice_key, next_step_id, result_document_type_id, result_summary_key, display_order)
SELECT s.id, 'aide.assistant.doc_child.acte', CAST(NULL AS INTEGER), (SELECT id FROM document_types WHERE libelle = 'Acte de naissance' LIMIT 1), 'aide.assistant.result.acte_naissance', 1
FROM assistant_steps s WHERE s.step_id = 'doc_child';

-- doc_identity -> Passeport, Carte d'identité, Certificat de résidence
INSERT INTO assistant_choices (step_id, choice_key, next_step_id, result_document_type_id, result_summary_key, display_order)
SELECT s.id, 'aide.assistant.doc_identity.passeport', CAST(NULL AS INTEGER), (SELECT id FROM document_types WHERE libelle = 'Passeport' LIMIT 1), 'aide.assistant.result.passeport', 1 FROM assistant_steps s WHERE s.step_id = 'doc_identity'
UNION ALL
SELECT s.id, 'aide.assistant.doc_identity.cni', CAST(NULL AS INTEGER), (SELECT id FROM document_types WHERE libelle = 'Carte d''identité' LIMIT 1), 'aide.assistant.result.cni', 2 FROM assistant_steps s WHERE s.step_id = 'doc_identity'
UNION ALL
SELECT s.id, 'aide.assistant.doc_identity.residence', CAST(NULL AS INTEGER), (SELECT id FROM document_types WHERE libelle = 'Certificat de résidence' LIMIT 1), 'aide.assistant.result.residence', 3 FROM assistant_steps s WHERE s.step_id = 'doc_identity';

-- doc_other -> Certificat d'hébergement, Certificat de célibat, Certificat de résidence
INSERT INTO assistant_choices (step_id, choice_key, next_step_id, result_document_type_id, result_summary_key, display_order)
SELECT s.id, 'aide.assistant.doc_other.hebergement', CAST(NULL AS INTEGER), (SELECT id FROM document_types WHERE libelle = 'Certificat d''hébergement' LIMIT 1), 'aide.assistant.result.hebergement', 1 FROM assistant_steps s WHERE s.step_id = 'doc_other'
UNION ALL
SELECT s.id, 'aide.assistant.doc_other.celibat', CAST(NULL AS INTEGER), (SELECT id FROM document_types WHERE libelle = 'Certificat de célibat' LIMIT 1), 'aide.assistant.result.celibat', 2 FROM assistant_steps s WHERE s.step_id = 'doc_other'
UNION ALL
SELECT s.id, 'aide.assistant.doc_other.residence', CAST(NULL AS INTEGER), (SELECT id FROM document_types WHERE libelle = 'Certificat de résidence' LIMIT 1), 'aide.assistant.result.residence', 3 FROM assistant_steps s WHERE s.step_id = 'doc_other';
