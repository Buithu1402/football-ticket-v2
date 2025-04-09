import {Inject, Injectable, Renderer2} from '@angular/core';
import {DOCUMENT} from '@angular/common';
import {LIB} from '../constant';

@Injectable({
  providedIn: 'root'
})
export class LibServiceService {

  constructor(private renderer: Renderer2,
              @Inject(DOCUMENT) private document: Document) {
  }


  admin() {
    this.process(LIB.admin.style, LIB.admin.script);
  }

  user() {
    this.process(LIB.user.lib1, LIB.user.script1);
  }

  process(rels: string[], scripts: string[]) {
    // Step 1: Remove all existing <link> tags with rel="stylesheet" from <head> that include 'app/share'
    const existingLinks = this.document.head.querySelectorAll('link[rel="stylesheet"]') as NodeListOf<HTMLLinkElement>;
    existingLinks.forEach((link: HTMLLinkElement) => {
      const href = link.getAttribute('href') || '';
      if (href && href.includes('app/share')) {
        this.renderer.removeChild(this.document.head, link);
      }
    });

    // Step 2: Remove all existing <script> tags from <body> that include 'app/share'
    const existingScripts = this.document.body.querySelectorAll('script') as NodeListOf<HTMLScriptElement>;
    existingScripts.forEach((script: HTMLScriptElement) => {
      const src = script.getAttribute('src') || '';
      if (src && src.includes('app/share')) {
        this.renderer.removeChild(this.document.body, script);
      }
    });
    rels.forEach((styleUrl: string) => {
      const link = this.renderer.createElement('link');
      this.renderer.setAttribute(link, 'rel', 'stylesheet');
      this.renderer.setAttribute(link, 'href', styleUrl);
      this.renderer.appendChild(this.document.head, link);
    });

    scripts.forEach((scriptUrl: string) => {
      const script = this.renderer.createElement('script');
      this.renderer.setAttribute(script, 'src', scriptUrl);
      this.renderer.appendChild(this.document.body, script);
    });
  }
}
