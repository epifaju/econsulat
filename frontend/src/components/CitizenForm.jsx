import React, { useState } from "react";
import {
  Card,
  Form,
  Button,
  Alert,
  Container,
  Row,
  Col,
  ProgressBar,
} from "react-bootstrap";
import { toast } from "react-toastify";
import axios from "axios";

// Configuration axios avec le token JWT
const api = axios.create({
  baseURL: "http://localhost:8080",
});

// Intercepteur pour ajouter le token JWT à chaque requête
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

const CitizenForm = () => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    birthDate: "",
    birthPlace: "",
    documentType: "ACTE_NAISSANCE",
  });
  const [selectedFile, setSelectedFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [citizenId, setCitizenId] = useState(null);
  const [uploadProgress, setUploadProgress] = useState(0);

  const documentTypes = [
    { value: "ACTE_NAISSANCE", label: "Acte de naissance" },
    { value: "PASSEPORT", label: "Passeport" },
    { value: "CERTIFICAT", label: "Certificat" },
    { value: "AUTRE", label: "Autre document" },
  ];

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      // Vérifier la taille du fichier (10MB max)
      if (file.size > 10 * 1024 * 1024) {
        toast.error("Le fichier est trop volumineux. Taille maximale: 10MB");
        e.target.value = "";
        return;
      }
      setSelectedFile(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setUploadProgress(0);

    try {
      // Créer la demande
      const response = await api.post("/api/citizens", formData);
      const citizen = response.data;
      setCitizenId(citizen.id);
      setUploadProgress(50);

      // Upload du fichier si sélectionné
      if (selectedFile) {
        const formDataFile = new FormData();
        formDataFile.append("file", selectedFile);

        await api.post(`/api/citizens/${citizen.id}/upload`, formDataFile, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
          onUploadProgress: (progressEvent) => {
            const progress = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            setUploadProgress(50 + progress * 0.5); // 50-100%
          },
        });
      }

      setUploadProgress(100);
      toast.success("Demande soumise avec succès!");

      // Réinitialiser le formulaire
      setFormData({
        firstName: "",
        lastName: "",
        birthDate: "",
        birthPlace: "",
        documentType: "ACTE_NAISSANCE",
      });
      setSelectedFile(null);
      setCitizenId(null);
      setUploadProgress(0);
    } catch (error) {
      toast.error(
        error.response?.data?.message || "Erreur lors de la soumission"
      );
      setUploadProgress(0);
    }

    setLoading(false);
  };

  return (
    <Container fluid className="py-3">
      <Row className="justify-content-center">
        <Col xs={12} sm={10} md={8} lg={6} xl={5}>
          <Card className="shadow-lg border-0">
            <Card.Header className="bg-primary text-white text-center border-0">
              <div className="d-flex align-items-center justify-content-center mb-2">
                <span className="h3 mb-0 me-2">📝</span>
                <div>
                  <h4 className="mb-1">Demande de document consulaire</h4>
                  <p className="mb-0 small opacity-75">
                    Formulaire de demande pour citoyen
                  </p>
                </div>
              </div>
              <div className="d-flex justify-content-end">
                <Button
                  variant="outline-light"
                  size="sm"
                  href="/"
                  className="d-flex align-items-center"
                >
                  <span className="me-1">📋</span>
                  <span className="d-none d-sm-inline">Mes Demandes</span>
                  <span className="d-inline d-sm-none">Demandes</span>
                </Button>
              </div>
            </Card.Header>
            <Card.Body className="p-4">
              {loading && (
                <div className="mb-3">
                  <div className="d-flex justify-content-between align-items-center mb-2">
                    <small className="text-muted">
                      Progression de la soumission
                    </small>
                    <small className="text-muted">
                      {Math.round(uploadProgress)}%
                    </small>
                  </div>
                  <ProgressBar
                    now={uploadProgress}
                    variant="primary"
                    animated
                    className="mb-3"
                  />
                </div>
              )}

              {citizenId && (
                <Alert variant="success" className="d-flex align-items-center">
                  <span className="me-2">✅</span>
                  <div>
                    <strong>Demande créée!</strong> Votre numéro de demande est:{" "}
                    <strong className="text-primary">#{citizenId}</strong>
                  </div>
                </Alert>
              )}

              <Form onSubmit={handleSubmit}>
                <Row>
                  <Col xs={12} sm={6}>
                    <Form.Group className="mb-3">
                      <Form.Label className="fw-semibold">
                        Prénom <span className="text-danger">*</span>
                      </Form.Label>
                      <Form.Control
                        type="text"
                        name="firstName"
                        value={formData.firstName}
                        onChange={handleChange}
                        required
                        placeholder="Entrez votre prénom"
                        className="form-control-lg"
                      />
                    </Form.Group>
                  </Col>
                  <Col xs={12} sm={6}>
                    <Form.Group className="mb-3">
                      <Form.Label className="fw-semibold">
                        Nom de famille <span className="text-danger">*</span>
                      </Form.Label>
                      <Form.Control
                        type="text"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleChange}
                        required
                        placeholder="Entrez votre nom"
                        className="form-control-lg"
                      />
                    </Form.Group>
                  </Col>
                </Row>

                <Row>
                  <Col xs={12} sm={6}>
                    <Form.Group className="mb-3">
                      <Form.Label className="fw-semibold">
                        Date de naissance <span className="text-danger">*</span>
                      </Form.Label>
                      <Form.Control
                        type="date"
                        name="birthDate"
                        value={formData.birthDate}
                        onChange={handleChange}
                        required
                        className="form-control-lg"
                      />
                    </Form.Group>
                  </Col>
                  <Col xs={12} sm={6}>
                    <Form.Group className="mb-3">
                      <Form.Label className="fw-semibold">
                        Lieu de naissance <span className="text-danger">*</span>
                      </Form.Label>
                      <Form.Control
                        type="text"
                        name="birthPlace"
                        value={formData.birthPlace}
                        onChange={handleChange}
                        required
                        placeholder="Ville, Pays"
                        className="form-control-lg"
                      />
                    </Form.Group>
                  </Col>
                </Row>

                <Form.Group className="mb-3">
                  <Form.Label className="fw-semibold">
                    Type de document <span className="text-danger">*</span>
                  </Form.Label>
                  <Form.Select
                    name="documentType"
                    value={formData.documentType}
                    onChange={handleChange}
                    required
                    className="form-select-lg"
                  >
                    {documentTypes.map((type) => (
                      <option key={type.value} value={type.value}>
                        {type.label}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>

                <Form.Group className="mb-4">
                  <Form.Label className="fw-semibold">
                    Document justificatif{" "}
                    <span className="text-muted">(optionnel)</span>
                  </Form.Label>
                  <Form.Control
                    type="file"
                    onChange={handleFileChange}
                    accept=".pdf,.jpg,.jpeg,.png"
                    className="form-control-lg"
                  />
                  <Form.Text className="text-muted">
                    <small>
                      📎 Formats acceptés: PDF, JPG, JPEG, PNG (max 10MB)
                      {selectedFile && (
                        <div className="mt-1">
                          <strong>Fichier sélectionné:</strong>{" "}
                          {selectedFile.name}
                        </div>
                      )}
                    </small>
                  </Form.Text>
                </Form.Group>

                <Button
                  variant="primary"
                  type="submit"
                  className="w-100 btn-lg"
                  disabled={loading}
                >
                  {loading ? (
                    <span className="d-flex align-items-center justify-content-center">
                      <span
                        className="spinner-border spinner-border-sm me-2"
                        role="status"
                      ></span>
                      Soumission en cours...
                    </span>
                  ) : (
                    <span className="d-flex align-items-center justify-content-center">
                      <span className="me-2">📤</span>
                      Soumettre la demande
                    </span>
                  )}
                </Button>
              </Form>

              <hr className="my-4" />

              <div className="text-center text-muted">
                <div className="small">
                  <span className="text-danger">*</span> Champs obligatoires
                </div>
                <div className="small mt-1">
                  Votre demande sera traitée par nos agents consulaires.
                </div>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default CitizenForm;
