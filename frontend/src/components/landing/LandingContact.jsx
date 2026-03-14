import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "react-toastify";
import ScrollReveal from "./ScrollReveal";

const LandingContact = ({ contactData, focusRing }) => {
  const { t } = useTranslation();
  const [form, setForm] = useState({ name: "", email: "", subject: "", message: "" });
  const [sending, setSending] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name.trim() || !form.email.trim() || !form.message.trim()) {
      toast.error(t("landing.contact.formRequired"));
      return;
    }
    setSending(true);
    try {
      // Pas d'API contact pour l'instant : simulation succès (à brancher sur un endpoint plus tard)
      await new Promise((r) => setTimeout(r, 800));
      toast.success(t("landing.contact.formSuccess"));
      setForm({ name: "", email: "", subject: "", message: "" });
    } catch (_) {
      toast.error(t("landing.contact.formError"));
    } finally {
      setSending(false);
    }
  };

  return (
    <section id="contact" className="py-16 sm:py-24 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <ScrollReveal className="text-center mb-14">
          <h2 className="text-3xl font-bold text-gray-900">{t("landing.contact.title")}</h2>
          <p className="mt-4 text-lg text-gray-700 max-w-2xl mx-auto">{t("landing.contact.subtitle")}</p>
        </ScrollReveal>
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8 mb-14">
          {contactData.map(({ Icon, titleKey, contentKey, type, href, preLine, breakAll }, index) => (
            <ScrollReveal key={titleKey} delay={index * 80}>
              <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
                <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                  <Icon className="h-6 w-6 text-primary-600" />
                </div>
                <h3 className="mt-4 font-semibold text-gray-900">{t(titleKey)}</h3>
                {type === "text" ? (
                  <p className={`mt-2 text-sm text-gray-700 ${preLine ? "whitespace-pre-line" : ""}`}>
                    {t(contentKey)}
                  </p>
                ) : (
                  <a
                    href={href}
                    className={`mt-2 text-sm text-gray-700 hover:text-primary-600 block rounded ${breakAll ? "break-all" : ""} ${focusRing}`}
                  >
                    {t(contentKey)}
                  </a>
                )}
              </div>
            </ScrollReveal>
          ))}
        </div>
        <ScrollReveal>
          <div className="max-w-xl mx-auto rounded-xl border border-gray-200 bg-gray-50/50 p-6 sm:p-8">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">{t("landing.contact.formTitle")}</h3>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label htmlFor="contact-name" className="block text-sm font-medium text-gray-700 mb-1">
                  {t("landing.contact.formName")}
                </label>
                <input
                  id="contact-name"
                  name="name"
                  type="text"
                  value={form.name}
                  onChange={handleChange}
                  placeholder={t("landing.contact.formNamePlaceholder")}
                  className="w-full rounded-lg border border-gray-300 px-3 py-2 text-gray-900 placeholder-gray-500 focus:border-primary-500 focus:ring-1 focus:ring-primary-500 focus:outline-none"
                  required
                />
              </div>
              <div>
                <label htmlFor="contact-email" className="block text-sm font-medium text-gray-700 mb-1">
                  {t("landing.contact.formEmail")}
                </label>
                <input
                  id="contact-email"
                  name="email"
                  type="email"
                  value={form.email}
                  onChange={handleChange}
                  placeholder={t("landing.contact.formEmailPlaceholder")}
                  className="w-full rounded-lg border border-gray-300 px-3 py-2 text-gray-900 placeholder-gray-500 focus:border-primary-500 focus:ring-1 focus:ring-primary-500 focus:outline-none"
                  required
                />
              </div>
              <div>
                <label htmlFor="contact-subject" className="block text-sm font-medium text-gray-700 mb-1">
                  {t("landing.contact.formSubject")}
                </label>
                <input
                  id="contact-subject"
                  name="subject"
                  type="text"
                  value={form.subject}
                  onChange={handleChange}
                  placeholder={t("landing.contact.formSubjectPlaceholder")}
                  className="w-full rounded-lg border border-gray-300 px-3 py-2 text-gray-900 placeholder-gray-500 focus:border-primary-500 focus:ring-1 focus:ring-primary-500 focus:outline-none"
                />
              </div>
              <div>
                <label htmlFor="contact-message" className="block text-sm font-medium text-gray-700 mb-1">
                  {t("landing.contact.formMessage")}
                </label>
                <textarea
                  id="contact-message"
                  name="message"
                  rows={4}
                  value={form.message}
                  onChange={handleChange}
                  placeholder={t("landing.contact.formMessagePlaceholder")}
                  className="w-full rounded-lg border border-gray-300 px-3 py-2 text-gray-900 placeholder-gray-500 focus:border-primary-500 focus:ring-1 focus:ring-primary-500 focus:outline-none resize-y min-h-[100px]"
                  required
                />
              </div>
              <button
                type="submit"
                disabled={sending}
                className={`w-full sm:w-auto px-6 py-2.5 rounded-lg text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 disabled:opacity-60 disabled:cursor-not-allowed ${focusRing}`}
              >
                {sending ? t("landing.contact.formSending") : t("landing.contact.formSubmit")}
              </button>
            </form>
          </div>
        </ScrollReveal>
      </div>
    </section>
  );
};

export default LandingContact;
