import React from "react";

const StatsCard = ({
  title,
  value,
  change,
  changeType = "neutral",
  icon: Icon,
  description,
}) => {
  const getChangeColor = () => {
    switch (changeType) {
      case "positive":
        return "text-secondary-600";
      case "negative":
        return "text-red-600";
      default:
        return "text-gray-600";
    }
  };

  const getChangeIcon = () => {
    switch (changeType) {
      case "positive":
        return "↗";
      case "negative":
        return "↘";
      default:
        return "→";
    }
  };

  return (
    <div className="stats-card">
      <div className="stats-card-header">
        <div className="flex items-center">
          <div className="stats-card-icon">
            <Icon className="h-6 w-6 text-gray-400" aria-hidden="true" />
          </div>
          <div className="stats-card-content">
            <p className="stats-card-title">{title}</p>
            <p className="stats-card-value">{value}</p>
            {change && (
              <p className={`text-sm ${getChangeColor()}`}>
                <span className="font-medium">
                  {getChangeIcon()} {change}
                </span>
                {description && (
                  <span className="text-gray-500 ml-1">{description}</span>
                )}
              </p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default StatsCard;
