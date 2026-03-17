-- FAQ: catégories et entrées (texte via clés i18n côté frontend)
CREATE TABLE IF NOT EXISTS faq_categories (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS faq_entries (
    id SERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL REFERENCES faq_categories(id) ON DELETE CASCADE,
    question_key VARCHAR(255) NOT NULL,
    answer_key VARCHAR(255) NOT NULL,
    keywords VARCHAR(500),
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Assistant: étapes et choix (arbre décisionnel)
CREATE TABLE IF NOT EXISTS assistant_steps (
    id SERIAL PRIMARY KEY,
    step_id VARCHAR(80) NOT NULL UNIQUE,
    question_key VARCHAR(255) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS assistant_choices (
    id SERIAL PRIMARY KEY,
    step_id INTEGER NOT NULL REFERENCES assistant_steps(id) ON DELETE CASCADE,
    choice_key VARCHAR(255) NOT NULL,
    next_step_id INTEGER REFERENCES assistant_steps(id) ON DELETE SET NULL,
    result_document_type_id INTEGER REFERENCES document_types(id) ON DELETE SET NULL,
    result_summary_key VARCHAR(255),
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_next_or_result CHECK (
        (next_step_id IS NOT NULL AND result_document_type_id IS NULL)
        OR (next_step_id IS NULL AND result_document_type_id IS NOT NULL AND result_summary_key IS NOT NULL)
    )
);

-- Index pour les requêtes courantes
CREATE INDEX IF NOT EXISTS idx_faq_entries_category ON faq_entries(category_id);
CREATE INDEX IF NOT EXISTS idx_faq_categories_order ON faq_categories(display_order);
CREATE INDEX IF NOT EXISTS idx_faq_entries_order ON faq_entries(display_order);
CREATE INDEX IF NOT EXISTS idx_assistant_choices_step ON assistant_choices(step_id);
