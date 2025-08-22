import React, { useState, useEffect } from "react";

// Règles de validation prédéfinies
export const validationRules = {
  required: (value) =>
    value !== null && value !== undefined && value.toString().trim() !== "",
  email: (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
  minLength: (min) => (value) => value && value.toString().length >= min,
  maxLength: (max) => (value) => value && value.toString().length <= max,
  pattern: (regex) => (value) => regex.test(value),
  date: (value) => {
    if (!value) return false;
    const date = new Date(value);
    return date instanceof Date && !isNaN(date);
  },
  futureDate: (value) => {
    if (!value) return false;
    const date = new Date(value);
    const now = new Date();
    return date > now;
  },
  pastDate: (value) => {
    if (!value) return false;
    const date = new Date(value);
    const now = new Date();
    return date < now;
  },
  age: (minAge) => (value) => {
    if (!value) return false;
    const birthDate = new Date(value);
    const today = new Date();
    const age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();

    if (
      monthDiff < 0 ||
      (monthDiff === 0 && today.getDate() < birthDate.getDate())
    ) {
      return age - 1 >= minAge;
    }
    return age >= minAge;
  },
  fileSize: (maxSizeMB) => (files) => {
    if (!files || files.length === 0) return true;
    const maxSizeBytes = maxSizeMB * 1024 * 1024;
    return Array.from(files).every((file) => file.size <= maxSizeBytes);
  },
  fileType: (allowedTypes) => (files) => {
    if (!files || files.length === 0) return true;
    return Array.from(files).every((file) =>
      allowedTypes.some((type) => file.type.includes(type))
    );
  },
};

// Messages d'erreur par défaut
export const defaultMessages = {
  required: "Ce champ est obligatoire",
  email: "Veuillez saisir une adresse email valide",
  minLength: (min) => `Ce champ doit contenir au moins ${min} caractères`,
  maxLength: (max) => `Ce champ doit contenir au maximum ${max} caractères`,
  pattern: "Le format saisi n'est pas valide",
  date: "Veuillez saisir une date valide",
  futureDate: "La date doit être dans le futur",
  pastDate: "La date doit être dans le passé",
  age: (minAge) => `L'âge minimum requis est de ${minAge} ans`,
  fileSize: (maxSizeMB) =>
    `La taille maximale des fichiers est de ${maxSizeMB} MB`,
  fileType: (types) => `Types de fichiers autorisés : ${types.join(", ")}`,
};

// Hook de validation personnalisé
export const useFormValidation = (initialValues, validationSchema) => {
  const [values, setValues] = useState(initialValues);
  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});
  const [isValid, setIsValid] = useState(false);

  // Valider un champ spécifique
  const validateField = (name, value) => {
    if (!validationSchema[name]) return "";

    const fieldRules = validationSchema[name];
    let fieldError = "";

    for (const rule of fieldRules) {
      if (rule.validator && !rule.validator(value)) {
        fieldError =
          rule.message || defaultMessages[rule.type] || "Champ invalide";
        break;
      }
    }

    return fieldError;
  };

  // Valider tous les champs
  const validateAll = () => {
    const newErrors = {};
    let hasErrors = false;

    Object.keys(validationSchema).forEach((fieldName) => {
      const fieldError = validateField(fieldName, values[fieldName]);
      if (fieldError) {
        newErrors[fieldName] = fieldError;
        hasErrors = true;
      }
    });

    setErrors(newErrors);
    setIsValid(!hasErrors);
    return !hasErrors;
  };

  // Mettre à jour la valeur d'un champ
  const setValue = (name, value) => {
    setValues((prev) => ({ ...prev, [name]: value }));

    // Valider le champ si il a été touché
    if (touched[name]) {
      const fieldError = validateField(name, value);
      setErrors((prev) => ({ ...prev, [name]: fieldError }));
    }
  };

  // Marquer un champ comme touché
  const setTouchedField = (name) => {
    setTouched((prev) => ({ ...prev, [name]: true }));

    // Valider le champ
    const fieldError = validateField(name, values[name]);
    setErrors((prev) => ({ ...prev, [name]: fieldError }));
  };

  // Réinitialiser le formulaire
  const reset = () => {
    setValues(initialValues);
    setErrors({});
    setTouched({});
    setIsValid(false);
  };

  // Vérifier si le formulaire est valide
  useEffect(() => {
    const hasErrors = Object.keys(errors).some((key) => errors[key]);
    setIsValid(!hasErrors);
  }, [errors]);

  return {
    values,
    errors,
    touched,
    isValid,
    setValue,
    setTouchedField,
    validateField,
    validateAll,
    reset,
  };
};

// Composant de champ de formulaire avec validation
export const FormField = ({
  name,
  label,
  type = "text",
  placeholder,
  validation,
  options = [],
  ...props
}) => {
  const { values, errors, touched, setValue, setTouchedField } = validation;
  const hasError = touched[name] && errors[name];

  const handleChange = (e) => {
    const value = type === "file" ? e.target.files : e.target.value;
    setValue(name, value);
  };

  const handleBlur = () => {
    setTouchedField(name);
  };

  const renderField = () => {
    switch (type) {
      case "select":
        return (
          <select
            name={name}
            value={values[name] || ""}
            onChange={handleChange}
            onBlur={handleBlur}
            className={`block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 ${
              hasError ? "border-red-300" : "border-gray-300"
            }`}
            {...props}
          >
            <option value="">{placeholder || "Sélectionnez..."}</option>
            {options.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        );

      case "textarea":
        return (
          <textarea
            name={name}
            value={values[name] || ""}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder={placeholder}
            className={`block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 ${
              hasError ? "border-red-300" : "border-gray-300"
            }`}
            {...props}
          />
        );

      case "file":
        return (
          <input
            type="file"
            name={name}
            onChange={handleChange}
            onBlur={handleBlur}
            className={`block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 ${
              hasError ? "border-red-300" : "border-gray-300"
            }`}
            {...props}
          />
        );

      default:
        return (
          <input
            type={type}
            name={name}
            value={values[name] || ""}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder={placeholder}
            className={`block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 ${
              hasError ? "border-red-300" : "border-gray-300"
            }`}
            {...props}
          />
        );
    }
  };

  return (
    <div className="mb-4">
      {label && (
        <label
          htmlFor={name}
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          {label}
        </label>
      )}
      {renderField()}
      {hasError && <p className="mt-1 text-sm text-red-600">{errors[name]}</p>}
    </div>
  );
};

export default FormField;
