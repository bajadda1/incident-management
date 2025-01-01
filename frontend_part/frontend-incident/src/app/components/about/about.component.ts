import {Component} from '@angular/core';

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrl: './about.component.css',
  standalone: false
})
export class AboutComponent {

  faqs = [
    {
      question: 'What is the purpose of this platform',
      answer: 'Our platform aims to empower communities by enabling citizens to report, track, and resolve local incidents through GIS technology.',
      open: false,
    },
    {
      question: 'How can I report an incident',
      answer: 'You can report an incident directly through our platform by filling out a simple form and providing necessary details like location and type of incident.',
      open: false,
    },
    {
      question: 'Can I track the status of my reported incidents',
      answer: 'Yes, our platform allows you to track the real-time status and updates of any reported incident.',
      open: false,
    },
    {
      question: 'Who handles the reported incidents',
      answer: 'Reported incidents are directed to the relevant local authorities or community managers for resolution.',
      open: false,
    },
    {
      question: 'Is this platform free to use',
      answer: 'Yes, our platform is completely free for citizens to report and track incidents in their community.',
      open: false,
    },
    {
      question: 'Can I suggest improvements or features for the platform',
      answer: 'Absolutely! We value community feedback. You can contact us via the platform’s feedback section to share your suggestions.',
      open: false,
    },
  ];
  allExpanded!: boolean;


  teams = [
    {
      name: "Ahmed BAJADDA",
      profession: "Backend Developer",
      imageURL: "assets/team/bajadda.jpg",
      fbURL: "",
      twitterURL: "",
      linkedInURL: "https://www.linkedin.com/in/ahmed-bajadda/"
    },
    {
      name: "Oussama Mazouz",
      profession: "Frontend Developer",
      imageURL: "assets/team/mazouz.jpg",
      fbURL: "",
      twitterURL: "",
      linkedInURL: "https://www.linkedin.com/in/oussama-mazouz-993a32241/"
    },
  ];

  toggle(item: any) {
    item.open = !item.open;
  }

  toggleAll() {
    this.allExpanded = !this.allExpanded;
    this.faqs.forEach(item => (item.open = this.allExpanded));
  }
}
