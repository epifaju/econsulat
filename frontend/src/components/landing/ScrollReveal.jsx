import React from "react";
import { useInView } from "../../hooks/useInView";
import { usePrefersReducedMotion } from "../../hooks/usePrefersReducedMotion";

/**
 * Wrapper qui anime les enfants au scroll (fade + slide up) quand ils entrent dans le viewport.
 * Respecte prefers-reduced-motion : pas d'animation si l'utilisateur préfère réduire les mouvements.
 */
const ScrollReveal = ({ children, className = "", delay = 0 }) => {
  const [ref, isInView] = useInView({ rootMargin: "0px 0px -60px 0px", threshold: 0.05 });
  const prefersReducedMotion = usePrefersReducedMotion();

  const visible = prefersReducedMotion || isInView;
  const animate = !prefersReducedMotion;

  return (
    <div
      ref={ref}
      className={`${animate ? "transition-all duration-700 ease-out" : ""} ${
        visible ? "opacity-100 translate-y-0" : "opacity-0 translate-y-6"
      } ${className}`}
      style={animate && delay && visible ? { transitionDelay: `${delay}ms` } : undefined}
    >
      {children}
    </div>
  );
};

export default ScrollReveal;
