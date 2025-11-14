"use client";

import { AuthButton } from "@/components/AuthButton/AuthButton";
import { ChatContainer } from "@/components/ChatContainer/ChatContainer";
import { MessageInput } from "@/components/MessageInput/MessageInput";
import { Scenarios } from "@/components/Scenarios/Scenarios";
import { authService } from "@/services/auth";
import { useEffect, useState } from "react";
import c from "./page.module.css";

declare global {
  interface Window {
    MaxWebApp?: {
      initData?: string;
      initDataUnsafe?: {
        user?: {
          id: number;
          first_name?: string;
          last_name?: string;
          username?: string;
        };
      };
      ready?: () => void;
      expand?: () => void;
    };
  }
}

export default function Home() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isWebAppInitialized, setIsWebAppInitialized] = useState(false);

  useEffect(() => {
    const initWebApp = async () => {
      if (typeof window !== 'undefined' && window.MaxWebApp) {
        if (window.MaxWebApp.ready) {
          window.MaxWebApp.ready();
        }
        
        if (window.MaxWebApp.expand) {
          window.MaxWebApp.expand();
        }
        
        setIsWebAppInitialized(true);
        
        const userData = window.MaxWebApp.initDataUnsafe?.user;
        if (userData && userData.id) {
          try {
            await authService.signIn(userData.id);
            setIsAuthenticated(true);
          } catch (error) {
            console.error('Auto sign-in failed:', error);
          }
        }
      }
    };

    setIsAuthenticated(authService.isAuthenticated());

    const timer = setTimeout(initWebApp, 1000);

    const checkAuth = () => {
      setIsAuthenticated(authService.isAuthenticated());
    };

    const interval = setInterval(checkAuth, 1000);
    
    return () => {
      clearTimeout(timer);
      clearInterval(interval);
    };
  }, []);

  return (
    <div className={c.page}>
      <header className={c.header}>
        <h1 className={c.aiName}>CyberEDU</h1>
        <div className={c.authButtonContainer}>
          <AuthButton />
        </div>
      </header>

      <Scenarios isAuthenticated={isAuthenticated} />

      <ChatContainer />

      <MessageInput />
    </div>
  );
}
